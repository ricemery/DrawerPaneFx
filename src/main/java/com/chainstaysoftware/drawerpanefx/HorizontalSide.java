package com.chainstaysoftware.drawerpanefx;

import javafx.geometry.Orientation;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Child Pane to hold onto {@link DrawerNode} instances that are
 * rendered on the {@link Position#Left} or {@link Position#Right}
 * sides of a {@link DrawerPane}
 */
class HorizontalSide extends AbstractSide {
   private final VBox vBox;

   HorizontalSide(final Position position,
                  final DragState dragState) {
      super(position, dragState);

      if (!Position.Top.equals(position) && !Position.Bottom.equals(position)) {
         throw new IllegalArgumentException("Invalid position for HorizontalSide - " + position);
      }

      vBox = new VBox();
      vBox.setId("horizontalSideVBox");
      vBox.setFillWidth(true);
      vBox.maxWidthProperty().bind(widthProperty());

      init();
   }

   @Override
   protected Pane getPane() {
      return vBox;
   }

   @Override
   protected double getRotation() {
      return 0;
   }

   @Override
   protected Orientation getOrientation() {
      return Orientation.HORIZONTAL;
   }
}
