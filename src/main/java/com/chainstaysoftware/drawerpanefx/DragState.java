package com.chainstaysoftware.drawerpanefx;

/**
 * Used to hold onto {@link DrawerNode} instance that is
 * being dragged as part of a drag and drop sequence.
 */
class DragState {
   private DrawerNode draggedNode;

   synchronized DrawerNode getDraggedNode() {
      return draggedNode;
   }

   synchronized void setDraggedNode(final DrawerNode draggedNode) {
      this.draggedNode = draggedNode;
   }
}
