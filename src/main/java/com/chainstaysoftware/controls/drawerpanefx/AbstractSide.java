package com.chainstaysoftware.controls.drawerpanefx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Abstract Child Pane to hold onto {@link DrawerNode} instances that are
 * rendered on a side of a {@link DrawerPane}
 */
abstract class AbstractSide extends Pane {
   // TODO: Allow setting max/min percentage of scene??
   //private static final int SPLITPANE_MIN_WIDTH = 100;
   private static final double MAX_PERCENTAGE_OF_SCENE = .30;
   private static final int DIVIDER_WIDTH = 5;
   private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("drawerpanefx");

   // Toolbar to contain the show/hide buttons for contained drawers.
   private final ToolBar toolBar = new ToolBar();
   private final HBox toolbarHbox = new HBox();
   // SplitPane to contain visible non-floating drawers,
   private final SplitPane splitPane = new SplitPane();
   private final Divider divider;

   private final Position position;
   private final DragState dragState;
   private final Region insertionSpacer = new Region();

   private boolean allowMultipleOpenDrawers = true;
   private boolean floatingSelectedChanged = false;

   AbstractSide(final Position position,
                final DragState dragState) {

      this.position = position;
      this.dragState = dragState;
      this.divider = new Divider();
   }

   /**
    * Initialization - must be called from within the constructor
    * of implementing classes. Only should be called AFTER the
    * implementing class can return a valid JFX instance from getPane().
    */
   protected void init() {
      toolbarHbox.setId("toolbarHbox");
      toolbarHbox.setRotate(getRotation());

      initToolbar();
      initDivider();

      getPane().getChildren().addAll(toolBar);

      getChildren().add(getPane());

      initSplitPane();
      setClippingRegion();

      insertionSpacer.getStyleClass().add("drawerpanefx-spacer");
   }

   /**
    * Initialize the {@link ToolBar} instance.
    */
   private void initToolbar() {
      final Group toolbarGroup = new Group();
      toolbarGroup.getChildren().add(toolbarHbox);
      toolBar.getItems().add(toolbarGroup);
      toolBar.setOrientation(Orientation.VERTICAL);
      toolBar.getStyleClass().setAll(isHorizontal()
         ? "drawerpanefx-horizontal-toolbar"
         : "drawerpanefx-vertical-toolbar");

      if (isHorizontal()) {
         toolBar.setMinHeight(20);
         toolBar.prefWidthProperty().bind(widthProperty());
      } else {
         toolBar.setMinWidth(20);
         toolBar.prefHeightProperty().bind(heightProperty());
      }

      toolBar.setOnDragOver(new ToolbarDragOverHandler());
      toolBar.setOnDragDropped(new ToolbarDragDroppedHandler());
      toolBar.setOnDragExited(event -> toolbarHbox.getChildren().remove(insertionSpacer));
   }

   /**
    * {@link EventHandler} for handling drag over events for the {@link ToolBar}.
    * Used to indicate if a drag event is valid for this {@link ToolBar}.
    * And, to visibly indicate where a {@link DrawerNode} will be dropped.
    */
   private class ToolbarDragOverHandler implements EventHandler<DragEvent> {
      @Override
      public void handle(final DragEvent event) {
         if (canAccept(event)) {
            // Remove the spacer if present.
            toolbarHbox.getChildren().remove(insertionSpacer);

            // Insert the spacer at the new proposed location.
            toolbarHbox.getChildren().stream()
               .filter(node -> node instanceof ToggleButton)
               .findFirst()
               .ifPresent(node -> insertionSpacer.setPrefWidth(((ToggleButton)node).getWidth()));
            final int i = findInsertPosition(event);
            toolbarHbox.getChildren().add(i, insertionSpacer);

            event.acceptTransferModes(TransferMode.MOVE);
         }

         event.consume();
      }

      /**
       * True of the event can be handled by the owning {@link ToolBar}.
       */
      private boolean canAccept(final DragEvent dragEvent) {
         final DrawerNode drawerNode = dragState.getDraggedNode();

         return dragEvent.getDragboard().hasContent(DrawerDataFormat.CLIPBOARD_CONTENT_FORMAT)
            && drawerNode.isValidPosition(position);
      }
   }

   /**
    * {@link EventHandler} for handling drag dropped events for the {@link ToolBar}.
    * Used to update the owning {@link ToolBar} with a dropped {@link DrawerNode}.
    */
   private class ToolbarDragDroppedHandler implements EventHandler<DragEvent> {
      @Override
      public void handle(final DragEvent event) {
         boolean success = false;

         final DrawerNode drawerNode = dragState.getDraggedNode();
         if (drawerNode != null) {
            // Remove the spacer.
            toolbarHbox.getChildren().remove(insertionSpacer);

            // If the dragged node is present, then remove from the current location
            toolbarHbox.getChildren().stream()
               .filter(node -> node.getUserData() == drawerNode)
               .findFirst()
               .ifPresent(node -> toolbarHbox.getChildren().remove(node));

            // Insert the dragged node into the new location.
            final int i = findInsertPosition(event);
            final ToggleButton newButton = createButton(drawerNode);
            toolbarHbox.getChildren().add(i, newButton);

            dragState.setDraggedNode(null);

            // Close open drawers if only allow single open.
            if (!allowMultipleOpenDrawers) {
               splitPane.getItems().clear();
               // Toggle buttons off.
               toolbarHbox.getChildren()
                  .stream()
                  .filter(btn -> !newButton.equals(btn)
                     && !((DrawerNode)btn.getUserData()).isFloating())
                  .forEach(btn -> ((ToggleButton)btn).setSelected(false));
            }

            // Update the split pane to include the dragged node
            // in the correct location (if visible and not floating.
            splitPane.getItems().clear();
            splitPane.getItems().setAll(toolbarHbox.getChildren().stream()
               .map(button -> (DrawerNode)button.getUserData())
               .filter(Node::isVisible)
               .filter(node -> !node.isFloating())
               .collect(Collectors.toList()));

            success = true;
         }

         event.setDropCompleted(success);
         event.consume();
      }
   }

   /**
    * Initialize the {@link SplitPane}.
    */
   private void initSplitPane() {
      splitPane.setOrientation(getOrientation());
      splitPane.getItems().addListener(new SplitPaneItemChangeListener());
   }

   /**
    * {@link ListChangeListener} for the {@link SplitPane} Children.
    * Used to show/hide the {@link SplitPane} and to set the divider positions
    * when new drawers are shown/hidden.
    */
   private class SplitPaneItemChangeListener implements ListChangeListener<Node> {
      @Override
      public void onChanged(Change<? extends Node> c) {
         final int numItems = splitPane.getItems().size();
         if (numItems == 0) {
            getPane().getChildren().remove(splitPane);
            getPane().getChildren().remove(divider);
            return;
         }

         if (Position.Top.equals(position) || Position.Left.equals(position)) {
            getPane().getChildren().setAll(toolBar, splitPane, divider);
         } else if (Position.Bottom.equals(position) || Position.Right.equals(position)) {
            getPane().getChildren().setAll(divider, splitPane, toolBar);
         }

         final double percent = 1.0 / numItems;
         for (int i = 1; i < numItems; i++) {
            splitPane.setDividerPosition(i - 1, i * percent);
         }
      }
   }

   /**
    * Sets clipping region so that children of this Side do not spill out.
    */
   private void setClippingRegion() {
      final Rectangle clipRectangle = new Rectangle();
      setClip(clipRectangle);
      layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
         clipRectangle.setWidth(newValue.getWidth());
         clipRectangle.setHeight(newValue.getHeight());
      });
   }

   protected abstract Pane getPane();

   /**
    * Rotation of this Pane.
    */
   protected abstract double getRotation();

   /**
    * Orientation of this Pane.
    */
   protected abstract Orientation getOrientation();

   /**
    * Adds a {@link DrawerNode} to the end of this Side.
    */
   void addNode(final DrawerNode node) {
      addNode(node, toolbarHbox.getChildren().size());
   }

   /**
    * Adds a {@link DrawerNode} to this side a position index (0 based).
    */
   void addNode(final DrawerNode node,
                final int index) {
      if (node == null) {
         return;
      }

      if (!node.isFloating() && node.isVisible()) {
         splitPane.getItems().add(index, node);
      }

      toolbarHbox.getChildren().add(index, createButton(node));
   }

   /**
    * Creates a {@link ToggleButton} for the passed in {@link DrawerNode}.
    */
   private ToggleButton createButton(final DrawerNode node) {
      final ToggleButton button = new ToggleButton(node.getTitle());
      button.setUserData(node);
      button.setSelected(node.isVisible());
      button.selectedProperty().addListener(new ButtonChangeListener(node));
      button.setOnDragDetected(new ButtonDragDetectedHandler(button, node));
      button.setOnDragDone(new ButtonDragDoneHandler(button, node));
      // TODO: Setting focus traversable to false because pressing button
      // in non-focused toolbar is not resulting in the select action. Turning
      // off focus traversable works around this issue.
      button.setFocusTraversable(false);
      button.getStyleClass().add(isHorizontal()
         ? "drawerpanefx-horizontal-button"
         : "drawerpanefx-vertical-button");

      if (node.canFloat()) {
         button.setContextMenu(createButtonContextMenu(node));
      }

      if (node.getIcon() != null) {
         button.setGraphic(new ImageView(node.getIcon()));
      }

      return button;
   }

   /**
    * {@link ChangeListener} for a drawer button. Used to show/hide the
    * related {@link DrawerNode} within this side.
    */
   private class ButtonChangeListener implements ChangeListener<Boolean> {
      private final DrawerNode node;

      ButtonChangeListener(final DrawerNode node) {
         this.node = node;
      }

      @Override
      public void changed(final ObservableValue<? extends Boolean> observable,
                          final Boolean oldValue,
                          final Boolean newValue) {
         if (newValue) {
            if (node.isFloating()) {
               floatWindow(node);
            } else {
               dockWindow(node);
            }
         } else {
            hideNodeInternal(node);
         }
      }
   }

   /**
    * {@link EventHandler} for detecting a drag operation on the related
    * {@link DrawerNode}. Used to start the JFX drag/drop.
    */
   private class ButtonDragDetectedHandler implements EventHandler<MouseEvent> {
      private final ToggleButton button;
      private final DrawerNode node;

      ButtonDragDetectedHandler(final ToggleButton button,
                                final DrawerNode node) {
         this.button = button;
         this.node = node;
      }

      @Override
      public void handle(final MouseEvent event) {
         final Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
         final ClipboardContent clipboardContent = new ClipboardContent();
         clipboardContent.put(DrawerDataFormat.CLIPBOARD_CONTENT_FORMAT, "foo");
         final SnapshotParameters snapshotParameters = new SnapshotParameters();
         snapshotParameters.setTransform(new Rotate(getRotation()));
         db.setContent(clipboardContent);
         db.setDragView(button.snapshot(snapshotParameters, null));
         dragState.setDraggedNode(node);
         dragState.setInitialPosition(toolbarHbox.getChildren().indexOf(button));

         toolbarHbox.getChildren().remove(button);

         event.consume();
      }
   }

   /**
    * {@link EventHandler} to handle drag operation completion for a
    * {@link DrawerNode}. Used to remove the dragged {@link DrawerNode}
    * from this Side. This is a NOOP if the {@link DrawerNode} was just
    * moved within this side.
    */
   private class ButtonDragDoneHandler implements EventHandler<DragEvent> {
      private final ToggleButton button;
      private final DrawerNode node;

      ButtonDragDoneHandler(final ToggleButton button,
                            final DrawerNode node) {
         this.button = button;
         this.node = node;
      }

      @Override
      public void handle(final DragEvent event) {
          if (!TransferMode.MOVE.equals(event.getTransferMode())) {
             handleNotDropped();
          } else {
             handleDropped();
          }
      }

      private void handleDropped() {
         // Delete the node if the node was moved into another side.
         if (!findButton(node).isPresent()){
            splitPane.getItems().remove(node);
         }
      }

      private void handleNotDropped() {
         // Not dropped on a toolbar...
         // add the button back to the toolbar.
         toolbarHbox.getChildren().add(dragState.getInitialPosition(), button);

         // If the node canFloat, and not dropped on a toolbar then
         // float the node.
         if (node.canFloat()) {
            button.getContextMenu().getItems().stream()
               .filter(menuItem -> menuItem.getText().equals(resourceBundle.getString("floatingmode.menuitem.txt")))
               .findFirst()
               .ifPresent(menuItem -> ((CheckMenuItem)menuItem).setSelected(true));
         }
      }
   }

   /**
    * Creates a {@link ContextMenu} for a drawer button.
    */
   private ContextMenu createButtonContextMenu(final DrawerNode node) {
      final CheckMenuItem floatingMenuItem
         = new CheckMenuItem(resourceBundle.getString("floatingmode.menuitem.txt"));

      floatingMenuItem.setSelected(node.isFloating());
      floatingMenuItem.selectedProperty()
         .addListener((observable, oldValue, newValue) -> {
            floatingSelectedChanged = !newValue;

            if (oldValue == newValue) {
               return;
            }

            if (node.isFloating()) {
               closeFloatingWindow(node);
            }

            node.setFloating(newValue);

            findButton(node).ifPresent(toggleButton -> {
               if (toggleButton.isSelected()) {
                  showNodeInternal(node);
               }
            });

            floatingSelectedChanged = false;
         });


      final ContextMenu contextMenu = new ContextMenu();
      contextMenu.getItems().addAll(floatingMenuItem);
      return contextMenu;
   }

   void showNode(final DrawerNode node) {
      if (node == null) {
         return;
      }

      findButton(node).ifPresent(button -> {
         if (button.isDisabled()) {
            return;

         }
         button.setSelected(true);
      });
   }

   /**
    * Show a {@link DrawerNode}. Assumes that the associated button
    * is selected.
    */
   private void showNodeInternal(final DrawerNode node) {
      if (node.isFloating()) {
         floatWindow(node);
      } else {
         dockWindow(node);
      }
   }

   /**
    * Float a {@link DrawerNode} within its own window.
    */
   private void floatWindow(final DrawerNode node) {
      node.setVisible(true);
      splitPane.getItems().remove(node);
      final Stage floatingWindow = new FloatingWindowBuilder().create(getScene().getWindow(), node);
      node.getFloatingX().ifPresent(floatingWindow::setX);
      node.getFloatingY().ifPresent(floatingWindow::setY);
      floatingWindow.setOnCloseRequest(event -> {
         if (!floatingSelectedChanged) {
            findButton(node).ifPresent(toggleButton -> toggleButton.setSelected(false));
         }

         final Pane parent = (Pane) node.getParent();
         if (parent != null) {
            parent.getChildren().clear();
         }
      });
      floatingWindow.xProperty().addListener((observable, oldValue, newValue)
         -> node.setFloatingX(newValue.doubleValue()));
      floatingWindow.yProperty().addListener((observable, oldValue, newValue)
         -> node.setFloatingY(newValue.doubleValue()));
      floatingWindow.show();
   }

   /**
    * Dock a {@link DrawerNode} within this side's {@link SplitPane} at
    * the appropriate location.
    */
   private void dockWindow(final DrawerNode node) {
      closeFloatingWindow(node);

      if (!allowMultipleOpenDrawers) {
         splitPane.getItems().clear();
         // Toggle buttons off.
         final Optional<ToggleButton> nodeButton = findButton(node);
         toolbarHbox.getChildren()
            .stream()
            .filter(btn -> !nodeButton.map(nodeBtn -> nodeBtn.equals(btn)).orElse(false)
               && !((DrawerNode)btn.getUserData()).isFloating())
            .forEach(btn -> ((ToggleButton)btn).setSelected(false));
      }

      node.setVisible(true);
      splitPane.getItems().add(findInsertPosition(node), node);
   }

   void hideNode(final DrawerNode node) {
      findButton(node).ifPresent(button -> button.setSelected(false));
   }

   /**
    * Hide a {@link DrawerNode}. Assumes the associated button is
    * already unselected.
    */
   private void hideNodeInternal(final DrawerNode node) {
      closeFloatingWindow(node);

      node.setVisible(false);
      splitPane.getItems().remove(node);
   }

   /**
    * Close a {@link DrawerNode}'s floating window - if necessary.
    */
   private void closeFloatingWindow(DrawerNode node) {
      if (node.isFloating() && node.getScene() != null) {
         final Window stage = node.getScene().getWindow();
         stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
      }
   }

   /**
    * Disable/enable a {@link DrawerNode}'s show/hide button. And, associated node if visible.
    */
   public void disable(final DrawerNode node,
                       final boolean disable) {
      node.setDisable(disable);
      findButton(node).ifPresent(b -> b.setDisable(disable));
   }

   /**
    * Find a button within this side's buttons.
    */
   private Optional<ToggleButton> findButton(final DrawerNode node) {
      final List<Node> buttons = toolbarHbox.getChildren();
      return buttons.stream()
         .filter(button -> button.getUserData().equals(node))
         .map(button -> (ToggleButton)button)
         .findFirst();
   }

   /**
    * Remove a {@link DrawerNode} from this side.
    */
   void removeNode(final DrawerNode node) {
      if (node == null) {
         return;
      }

      findButton(node)
         .ifPresent(button -> {
            toolbarHbox.getChildren().remove(button);
            closeFloatingWindow(node);
            splitPane.getItems().remove(node);
         });
   }

   /**
    * Determine the insert position for the {@link DrawerNode}
    * based on the location of the associated {@link DrawerNode}'s button
    * within the {@link ToolBar}.
    */
   private int findInsertPosition(final DrawerNode node) {
      int j = 0;
      for (Node btn : toolbarHbox.getChildren()) {
         final ToggleButton button = (ToggleButton)btn;
         if (button.getUserData() == node) {
            return j;
         }

         if (splitPane.getItems().contains(button.getUserData())) {
            j++;
         }
      }

      return j;
   }

   /**
    * Determine the insert position for the {@link DrawerNode}
    * based on the coordinates of the passed in {@link DragEvent}.
    */
   private int findInsertPosition(final DragEvent event) {
      return isHorizontal()
         ? findInsertPositionHorizontal(event)
         : findInsertPositionVertical(event);
   }

   /**
    * Determine the insert position for the {@link DrawerNode}
    * based on the coordinates of the passed in {@link DragEvent} if this is
    * a horizontal side.
    */
   private int findInsertPositionHorizontal(final DragEvent event) {
      final double x = event.getSceneX();
      int i = 0;
      for (Node node : toolbarHbox.getChildren()) {
         if (node instanceof ToggleButton && centerOfButton((ToggleButton)node) > x) {
            return i;
         }

         i++;
      }

      return i;
   }

   /**
    * Determine the insert position for the {@link DrawerNode}
    * based on the coordinates of the passed in {@link DragEvent} if this is
    * a vertical side.
    */
   private int findInsertPositionVertical(final DragEvent event) {
      final double y = event.getSceneY();
      int i = 0;
      for (Node node : toolbarHbox.getChildren()) {
         if (node instanceof ToggleButton && centerOfButton((ToggleButton)node) > y) {
            return i;
         }

         i++;
      }

      return i;
   }

   /**
    * Determine the center of the passed in button.
    */
   private double centerOfButton(final ToggleButton button) {
      return isHorizontal()
         ? centerOfButtonHorizontal(button)
         : centerOfButtonVertical(button);
   }

   /**
    * Determine the center of the passed in horizontal button.
    */
   private double centerOfButtonHorizontal(final ToggleButton button) {
      final Bounds boundsInScene = button.localToScene(button.getLayoutBounds());
      return boundsInScene.getMinX() + (button.getWidth() / 2);
   }

   /**
    * Determine the center of the passed in vertical button.
    */
   private double centerOfButtonVertical(final ToggleButton button) {
      final Bounds boundsInScene = button.localToScene(button.getLayoutBounds());
      return boundsInScene.getMinY() + (button.getWidth() / 2);
   }

   /**
    * True if this side is horizontal.
    */
   private boolean isHorizontal() {
      return Position.Top.equals(position) || Position.Bottom.equals(position);
   }

   /**
    * Call to indicate if this side supports multiple opened drawers or
    * a single opened drawer.
    * TODO: repaint side if value changes...
    */
   void setAllowMultipleOpenDrawers(boolean allowMultipleOpenDrawers) {
      this.allowMultipleOpenDrawers = allowMultipleOpenDrawers;
   }

   List<DrawerNode> getNodes() {
      return toolbarHbox.getChildren().stream()
         .map(button -> (DrawerNode)button.getUserData())
         .collect(Collectors.toList());
   }

   private void initDivider() {
      if (isHorizontal()) {
         divider.prefWidthProperty().bind(widthProperty());
         divider.setPrefHeight(DIVIDER_WIDTH);
      } else {
         divider.setPrefWidth(DIVIDER_WIDTH);
         divider.prefHeightProperty().bind(heightProperty());
      }
   }

   /**
    * Divider for dividing Side from Center of {@link DrawerPane}.
    * Includes mouse handling for resizing the Side.
    */
   private class Divider extends StackPane {
      Divider() {
         getStyleClass().setAll(isHorizontal()
            ? "drawerpanefx-horizontal-divider"
            : "drawerpanefx-vertical-divider");
         setCursor(isHorizontal() ? Cursor.V_RESIZE : Cursor.H_RESIZE);

         final EventHandler<MouseEvent> mouseHandler
            = isHorizontal()
               ? new MouseHandlerForHorizontal()
               : new MouseHandlerForVertical();
         setOnMouseMoved(mouseHandler);
         setOnMouseDragged(mouseHandler);
         setOnMousePressed(mouseHandler);
         setOnMouseReleased(mouseHandler);
      }

      /**
       * {@link EventHandler} for {@link SplitPane} mouse events if this is a vertical side.
       * Used to allow resizing of side.
       */
      private class MouseHandlerForVertical implements EventHandler<MouseEvent> {
         private boolean mousePressed = false;

         @Override
         public void handle(final MouseEvent mouseEvent) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
               mousePressed = true;
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)
               && mousePressed) {
               final Bounds bounds = Divider.this.localToScene(Divider.this.getLayoutBounds());
               final double mouseX = mouseEvent.getSceneX();
               final double xDelta = mouseX - bounds.getMinX();
               final double updatedWidth = splitPane.getWidth() + xDelta
                  * (Position.Right.equals(position) ? -1 : 1);

               if (updatedWidth / splitPane.getScene().getWidth() <= MAX_PERCENTAGE_OF_SCENE) {
                  splitPane.setPrefWidth(updatedWidth);
               }
               //splitPane.setMinWidth(SPLITPANE_MIN_WIDTH);
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
               mousePressed = false;
            }
         }
      }

      /**
       * {@link EventHandler} for {@link SplitPane} mouse events if this is a horizontal side.
       * Used to allow resizing of side.
       */
      private class MouseHandlerForHorizontal implements EventHandler<MouseEvent> {
         private boolean mousePressed = false;

         @Override
         public void handle(final MouseEvent mouseEvent) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
               mousePressed = true;
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)
               && mousePressed) {
               final Bounds bounds = Divider.this.localToScene(Divider.this.getLayoutBounds());
               final double mouseY = mouseEvent.getSceneY();
               final double yDelta = mouseY - bounds.getMinY();
               final double updatedHeight = splitPane.getHeight() + yDelta
                  * (Position.Bottom.equals(position) ? -1 : 1);

               if (updatedHeight / splitPane.getScene().getHeight() <= MAX_PERCENTAGE_OF_SCENE) {
                  splitPane.setPrefHeight(updatedHeight);
               }
               //splitPane.setMinWidth(SPLITPANE_MIN_WIDTH);
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
               mousePressed = false;
            }
         }
      }
   }
}
