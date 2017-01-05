package com.chainstaysoftware.drawerpanefx;

import javafx.application.Application;
import javafx.scene.Scene;
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
   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(final Stage primaryStage) throws Exception {
      primaryStage.setTitle("DrawerPaneFx Sample App");

      // Initialize the DrawerPane CSS. Should be done before
      // the DrawerPane is displayed.
      DrawerPane.initializeDefaultUserAgentStylesheet();
      final DrawerPane drawerPane = new DrawerPane();
      // Hide the top of the DrawerPane.
      drawerPane.setTopVisible(false);

      initCenter(drawerPane);
      initLeft(drawerPane);
      initRight(drawerPane);
      initBottom(drawerPane);

      // Add the DrawerPane to a BorderPane for display.
      final BorderPane borderPane = new BorderPane();
      borderPane.setCenter(drawerPane);
      borderPane.prefWidthProperty().bind(primaryStage.widthProperty());
      borderPane.prefHeightProperty().bind(primaryStage.heightProperty());

      // Show the scene.
      primaryStage.setScene(new Scene(borderPane, 800, 600));
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
      final DrawerNode textDrawerNode1
         = new DrawerNode(textArea1, "textArea1", getIcon("/icons8/1-32.png"), true,
            Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      // Create a DrawerNode that can only be moved to the left or right sides.
      // Initial state is open. Can be floated.
      final TextArea textArea2 = new TextArea("area2");
      textArea2.setPrefWidth(50);
      final DrawerNode textDrawerNode2
         = new DrawerNode(textArea2, "textArea2", getIcon("/icons8/2-32.png"), true,
            Arrays.asList(Position.Right, Position.Left));

      drawerPane.addLeft(textDrawerNode1, textDrawerNode2);
   }

   private void initRight(final DrawerPane drawerPane) {
      // Create a DrawerNode that can be moved to any side. Initial state is open.
      // Can be floated.
      final TextArea textArea3 = new TextArea("area3");
      textArea3.setPrefWidth(50);
      final DrawerNode textDrawerNode3
         = new DrawerNode(textArea3, "textArea3", getIcon("/icons8/3-32.png"), true,
         Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      drawerPane.addRight(textDrawerNode3);
   }

   private void initBottom(final DrawerPane drawerPane) {
      // Create a DrawerNode that can be moved to any side. Initial state is open.
      // Can be floated.
      final TextArea textArea4 = new TextArea("area4");
      textArea4.setPrefWidth(50);
      final DrawerNode textDrawerNode4
         = new DrawerNode(textArea4, "textArea4", getIcon("/icons8/4-32.png"), true,
         Collections.emptyList());

      // Create a DrawerNode that can be moved to any side.
      // Can be floated. DrawerNode will be disabled below.
      final TextArea textArea5 = new TextArea("area5");
      textArea5.setPrefWidth(50);
      final DrawerNode textDrawerNode5
         = new DrawerNode(textArea5, "textArea5", getIcon("/icons8/5-32.png"), true,
         Arrays.asList(Position.Top, Position.Right, Position.Bottom, Position.Left));

      // Create a DrawerNode that can be moved to any side. Initial state is closed.
      // Can not be floated.
      final TextArea textArea6 = new TextArea("area6");
      textArea6.setPrefWidth(50);
      final DrawerNode textDrawerNode6
         = new DrawerNode(textArea6, "textArea6", getIcon("/icons8/6-32.png"), false,
         Arrays.asList(Position.Bottom));
      textDrawerNode6.setVisible(false);

      drawerPane.addBottom(textDrawerNode4, textDrawerNode5, textDrawerNode6);
      // Set textDrawerNode5 to disabled.
      drawerPane.setNodeDisable(textDrawerNode5, true);
      // Only allow a single open window on the bottom side.
      drawerPane.setBottomAllowMultipleOpenDrawers(false);
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
