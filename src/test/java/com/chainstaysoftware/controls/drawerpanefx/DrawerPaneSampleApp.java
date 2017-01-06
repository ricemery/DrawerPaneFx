package com.chainstaysoftware.controls.drawerpanefx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

/**
 * Sample app for showing how to use {@link DrawerPane}.
 */
public class DrawerPaneSampleApp extends Application {

   private DrawerNode textDrawerNode1;
   private DrawerNode textDrawerNode2;
   private DrawerNode textDrawerNode3;
   private DrawerNode textDrawerNode4;
   private DrawerNode textDrawerNode5;
   private DrawerNode textDrawerNode6;

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(final Stage primaryStage) throws Exception {
      primaryStage.setTitle("DrawerPaneFx Sample App");

      final DrawerPane drawerPane = new DrawerPane();
      // Hide the top of the DrawerPane.
      drawerPane.setTopVisible(false);

      initCenter(drawerPane);
      initLeft(drawerPane);
      initRight(drawerPane);
      initBottom(drawerPane);

      // Add the DrawerPane to a BorderPane for display.
      final BorderPane borderPane = new BorderPane();
      borderPane.setTop(createMenuBar(drawerPane));
      borderPane.setCenter(drawerPane);
      borderPane.prefWidthProperty().bind(primaryStage.widthProperty());
      borderPane.prefHeightProperty().bind(primaryStage.heightProperty());

      final Scene scene = new Scene(borderPane, 800, 600);

      // Initialize the DrawerPane CSS. Should be done before
      // the DrawerPane is displayed.
      drawerPane.initDefaultStyleSheet(scene);

      // Show the scene.
      primaryStage.setScene(scene);
      primaryStage.sizeToScene();
      primaryStage.show();
   }

   private void initCenter(final DrawerPane drawerPane) {
      final TableView<String> tableView = new TableView<>();
      tableView.getColumns().addAll(new TableColumn<String, String>("A"),
         new TableColumn<String, String>("B"), new TableColumn<String, String>("C"));
      tableView.setPrefWidth(200);
      drawerPane.setCenter(tableView);
   }

   private void initLeft(final DrawerPane drawerPane) {
      // Create a DrawerNode that can be moved to any side. Initial state is open.
      // Can be floated.
      final TextArea textArea1 = new TextArea("area1");
      textArea1.setPrefWidth(50);
      textDrawerNode1 = new DrawerNode(textArea1, "textArea1", getIcon("/icons8/1-32.png"), true,
         Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      // Create a DrawerNode that can only be moved to the left or right sides.
      // Initial state is open. Can be floated.
      final TextArea textArea2 = new TextArea("area2");
      textArea2.setPrefWidth(50);
      textDrawerNode2 = new DrawerNode(textArea2, "textArea2", getIcon("/icons8/2-32.png"), true,
         Arrays.asList(Position.Right, Position.Left));

      drawerPane.addLeft(textDrawerNode1, textDrawerNode2);
   }

   private void initRight(final DrawerPane drawerPane) {
      // Create a DrawerNode that can be moved to any side. Initial state is open.
      // Can be floated.
      final TextArea textArea3 = new TextArea("area3");
      textArea3.setPrefWidth(50);
      textDrawerNode3 = new DrawerNode(textArea3, "textArea3", getIcon("/icons8/3-32.png"), true,
      Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      drawerPane.addRight(textDrawerNode3);
   }

   private void initBottom(final DrawerPane drawerPane) {
      // Create a DrawerNode that can be moved to any side. Initial state is open.
      // Can be floated.
      final TextArea textArea4 = new TextArea("area4");
      textArea4.setPrefWidth(50);
      textDrawerNode4 = new DrawerNode(textArea4, "textArea4", getIcon("/icons8/4-32.png"), true,
      Collections.emptyList());

      // Create a DrawerNode that can be moved to any side.
      // Can be floated. DrawerNode will be disabled below.
      final TextArea textArea5 = new TextArea("area5");
      textArea5.setPrefWidth(50);
      textDrawerNode5 = new DrawerNode(textArea5, "textArea5", getIcon("/icons8/5-32.png"), true,
      Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      // Create a DrawerNode that can be moved to any side. Initial state is closed.
      // Can not be floated.
      final TextArea textArea6 = new TextArea("area6");
      textArea6.setPrefWidth(50);
      textDrawerNode6 = new DrawerNode(textArea6, "textArea6", getIcon("/icons8/6-32.png"), false,
      Arrays.asList(Position.Bottom));
      textDrawerNode6.setVisible(false);

      drawerPane.addBottom(textDrawerNode4, textDrawerNode5, textDrawerNode6);
      // Set textDrawerNode5 to disabled.
      drawerPane.setNodeDisable(textDrawerNode5, true);
      // Only allow a single open window on the bottom side.
      drawerPane.setBottomAllowMultipleOpenDrawers(false);
   }

   /**
    * Create a MenuBar that allows the user to invoke the programatic Show/Hide
    * API against the various {@link DrawerNode}s.
    */
   private MenuBar createMenuBar(final DrawerPane drawerPane) {
      final MenuItem showItem1 = new MenuItem("Show textArea1");
      showItem1.setOnAction(actionEvent -> drawerPane.show(textDrawerNode1));
      final MenuItem showItem2 = new MenuItem("Show textArea2");
      showItem2.setOnAction(actionEvent -> drawerPane.show(textDrawerNode2));
      final MenuItem showItem3 = new MenuItem("Show textArea3");
      showItem3.setOnAction(actionEvent -> drawerPane.show(textDrawerNode3));
      final MenuItem showItem4 = new MenuItem("Show textArea4");
      showItem4.setOnAction(actionEvent -> drawerPane.show(textDrawerNode4));
      final MenuItem showItem5 = new MenuItem("Show textArea5");
      showItem5.setOnAction(actionEvent -> drawerPane.show(textDrawerNode5));
      final MenuItem showItem6 = new MenuItem("Show textArea6");
      showItem6.setOnAction(actionEvent -> drawerPane.show(textDrawerNode6));

      final Menu showMenu = new Menu("Show");
      showMenu.getItems().addAll(showItem1, showItem2, showItem3, showItem4, showItem5, showItem6);

      final MenuItem hideItem1 = new MenuItem("Hide textArea1");
      hideItem1.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode1));
      final MenuItem hideItem2 = new MenuItem("Hide textArea2");
      hideItem2.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode2));
      final MenuItem hideItem3 = new MenuItem("Hide textArea3");
      hideItem3.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode3));
      final MenuItem hideItem4 = new MenuItem("Hide textArea4");
      hideItem4.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode4));
      final MenuItem hideItem5 = new MenuItem("Hide textArea5");
      hideItem5.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode5));
      final MenuItem hideItem6 = new MenuItem("Hide textArea6");
      hideItem6.setOnAction(actionEvent -> drawerPane.hide(textDrawerNode6));

      final Menu hideMenu = new Menu("Hide");
      hideMenu.getItems().addAll(hideItem1, hideItem2, hideItem3, hideItem4, hideItem5, hideItem6);

      final MenuBar menuBar = new MenuBar();
      menuBar.getMenus().addAll(showMenu, hideMenu);

      return menuBar;
   }

   /**
    * Load an icon from the resource path. And, scale to 16 x 16.
    */
   private Image getIcon(final String iconResourcePath) {
      try (final InputStream inputStream = getClass().getResourceAsStream(iconResourcePath)) {
         if (inputStream == null) {
            throw new IllegalArgumentException(iconResourcePath + " is not a valid resource");
         }

         return new Image(inputStream, 16, 16, true, true);
      } catch (IOException exception) {
         throw new IllegalStateException("Error loading " + iconResourcePath);
      }
   }
}
