package com.chainstaysoftware.controls.drawerpanefx;

/**
 * Used to hold onto {@link DrawerNode} instance that is
 * being dragged as part of a drag and drop sequence.
 */
class DragState {
   private DrawerNode draggedNode;
   private int initialPosition;

   DrawerNode getDraggedNode() {
      return draggedNode;
   }

   void setDraggedNode(final DrawerNode draggedNode) {
      this.draggedNode = draggedNode;
   }

   int getInitialPosition() {
      return initialPosition;
   }

   void setInitialPosition(final int initialPosition) {
      this.initialPosition = initialPosition;
   }
}
