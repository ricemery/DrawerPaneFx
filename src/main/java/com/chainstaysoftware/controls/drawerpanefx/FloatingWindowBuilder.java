package com.chainstaysoftware.controls.drawerpanefx;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Builder to create floating window to contain a {@link DrawerNode}.
 */
class FloatingWindowBuilder {
   /**
    * Creates a {@link Stage} to contain the passed in {@link DrawerNode}.
    */
   Stage create(final Window parent,
                final DrawerNode node) {
      final Pane pane = new Pane(node);
      final Scene scene = new Scene(pane);

      node.getFloatStyleSheet().ifPresent(ss ->
         scene.getStylesheets().add(ss.toExternalForm()));

      final Stage stage = createStage(parent, node);
      stage.setScene(scene);
      return stage;
   }

   private Stage createStage(final Window parent,
                             final DrawerNode node) {
      final Stage stg = new Stage(StageStyle.DECORATED);
      // TODO: Size/position/icon
      //stg.setWidth(DIALOG_WIDTH);
      //stg.setHeight(DIALOG_HEIGHT);
      //stg.setX((owner.getX() + (owner.getWidth() / 2)) - (DIALOG_WIDTH / 2));
      //stg.setY((owner.getY() + (owner.getHeight() / 2)) - (DIALOG_HEIGHT / 2));
      stg.setResizable(true);
      stg.setTitle(node.getTitle());
      stg.initOwner(parent);
      stg.initModality(Modality.NONE);

      if (node.getIcon() != null) {
         stg.getIcons().add(node.getIcon());
      }

      return stg;
   }
}
