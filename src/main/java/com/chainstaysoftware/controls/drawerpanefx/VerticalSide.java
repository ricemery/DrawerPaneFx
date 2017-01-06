package com.chainstaysoftware.controls.drawerpanefx;

import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Child Pane to hold onto {@link DrawerNode} instances that are
 * rendered on the {@link Position#Top} or {@link Position#Bottom}
 * sides of a {@link DrawerPane}
 */
class VerticalSide extends AbstractSide {
   private final HBox hBox;

   VerticalSide(final Position position,
                final DragState dragState) {
      super(position, dragState);

      if (!Position.Left.equals(position) && !Position.Right.equals(position)) {
         throw new IllegalArgumentException("Invalid position for VerticalSide - " + position);
      }

      hBox = new HBox();
      hBox.setId("verticalSideVBox");
      hBox.setFillHeight(true);
      hBox.prefHeightProperty().bind(heightProperty());

      init();
   }

   @Override
   protected Pane getPane() {
      return hBox;
   }

   @Override
   protected double getRotation() {
      return 90.0;
   }

   @Override
   protected Orientation getOrientation() {
      return Orientation.VERTICAL;
   }
}
