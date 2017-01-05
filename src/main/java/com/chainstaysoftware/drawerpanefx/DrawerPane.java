package com.chainstaysoftware.drawerpanefx;

import com.sun.javafx.css.StyleManager;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Base class for JavaFx layout Pane that provides supports for drawers
 * (pane containers) that slide in from the Top, Right, Bottom and Left.
 * The drawers can optionally be detached from the DrawerPane and floated
 * within their own window. Drawers can also be dragged between the
 * sides of the pane.
 * Sliding the drawers in/out is controlled with buttons on toolbars that
 * surround the DrawerPane on the Top, Right, Bottom and Left. The toolbars
 * can optionally be disabled/enabled independently. And, the toolbars
 * and buttons can be styled using CSS.
 */
// TODO: Resize side panes.
public class DrawerPane extends Pane {
   private final DragState dragState = new DragState();
   private final BorderPane borderPane = new BorderPane();
   private final HorizontalSide top = new HorizontalSide(Position.Top, dragState);
   private final VerticalSide leftSide = new VerticalSide(Position.Left, dragState);
   private final HorizontalSide bottom = new HorizontalSide(Position.Bottom, dragState);
   private final VerticalSide rightSide = new VerticalSide(Position.Right, dragState);

   public DrawerPane() {
      borderPane.setTop(top);
      borderPane.setLeft(leftSide);
      borderPane.setBottom(bottom);
      borderPane.setRight(rightSide);

      getChildren().add(borderPane);

      setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
      borderPane.prefWidthProperty().bind(widthProperty());
      borderPane.prefHeightProperty().bind(heightProperty());
   }

   /**
    * Sets the {@link Node} placed at the center of this pane.
    */
   public void setCenter(final Node node) {
      borderPane.setCenter(node);
   }

   /**
    * Add {@link DrawerNode}s to the top of this pane.
    */
   public void addTop(final DrawerNode... nodes) {
      if (nodes == null) {
         return;
      }

      Arrays.stream(nodes).forEach(top::addNode);
   }

   /**
    * Add {@link DrawerNode}s to the right of this pane.
    */
   public void addRight(final DrawerNode... nodes) {
      if (nodes == null) {
         return;
      }

      Arrays.stream(nodes).forEach(rightSide::addNode);
   }

   /**
    * Add {@link DrawerNode}s to the bottom of this pane.
    */
   public void addBottom(final DrawerNode... nodes) {
      if (nodes == null) {
         return;
      }

      Arrays.stream(nodes).forEach(bottom::addNode);
   }

   /**
    * Add {@link DrawerNode}s to the left of this pane.
    */
   public void addLeft(final DrawerNode... nodes) {
      if (nodes == null) {
         return;
      }

      Arrays.stream(nodes).forEach(leftSide::addNode);
   }

   /**
    * Reveals a hidden node on whatever side the
    * {@link DrawerNode} is bound to (or floating).
    */
   public void show(final DrawerNode node) {
      getContainingSide(node).ifPresent(side -> side.showNode(node));
   }

   /**
    * Hides an opened {@link DrawerNode}.
    */
   public void hide(final DrawerNode node) {
      getContainingSide(node).ifPresent(side -> side.hideNode(node));
   }

   /**
    * Remove a {@link DrawerNode} from this pane.
    */
   public void remove(final DrawerNode node) {
      if (node == null) {
         return;
      }

      getContainingSide(node).ifPresent(side -> side.removeNode(node));
   }

   /**
    * Determines the {@link AbstractSide} that the passed in {@link DrawerNode}
    * is contained within.
    */
   private Optional<AbstractSide> getContainingSide(final DrawerNode node) {
      if (getTopNodes().contains(node)) {
         return Optional.of(top);
      }

      if (getRightNodes().contains(node)) {
         return Optional.of(rightSide);
      }

      if (getBottomNodes().contains(node)) {
         return Optional.of(bottom);
      }

      if (getLeftNodes().contains(node)) {
         return Optional.of(leftSide);
      }

      return Optional.empty();
   }

   /**
    * Get the {@link DrawerNode}s from the top of this pane.
    */
   public List<DrawerNode> getTopNodes() {
      return Collections.unmodifiableList(top.getNodes());
   }

   /**
    * Get the {@link DrawerNode}s from the right of this pane.
    */
   public List<DrawerNode> getRightNodes() {
      return Collections.unmodifiableList(rightSide.getNodes());
   }

   /**
    * Get the {@link DrawerNode}s from the bottom of this pane.
    */
   public List<DrawerNode> getBottomNodes() {
      return Collections.unmodifiableList(bottom.getNodes());
   }

   /**
    * Get the {@link DrawerNode}s from the left of this pane.
    */
   public List<DrawerNode> getLeftNodes() {
      return Collections.unmodifiableList(leftSide.getNodes());
   }

   /**
    * Toggle the top toolbar and {@link DrawerPane}s visible/invisible.
    */
   public void setTopVisible(final boolean visible) {
      borderPane.setTop(visible ? top : null);
   }

   /**
    * Toggle the right toolbar and {@link DrawerPane}s visible/invisible.
    */
   public void setRightVisible(final boolean visible) {
      borderPane.setRight(visible ? rightSide : null);
   }

   /**
    * Toggle the bottom toolbar and {@link DrawerPane}s visible/invisible.
    */
   public void setBottomVisible(final boolean visible) {
      borderPane.setBottom(visible ? bottom : null);
   }

   /**
    * Toggle the left toolbar and {@link DrawerPane}s visible/invisible.
    */
   public void setLeftVisible(final boolean visible) {
      borderPane.setLeft(visible ? leftSide : null);
   }

   /**
    * True to allow multiple drawers to be open within the top of
    * the pane. False to only allow a single drawer to be open.
    */
   public void setTopAllowMultipleOpenDrawers(final boolean allow) {
      top.setAllowMultipleOpenDrawers(allow);
   }

   /**
    * True to allow multiple drawers to be open within the right of
    * the pane. False to only allow a single drawer to be open.
    */
   public void setRightAllowMultipleOpenDrawers(final boolean allow) {
      rightSide.setAllowMultipleOpenDrawers(allow);
   }

   /**
    * True to allow multiple drawers to be open within the bottom of
    * the pane. False to only allow a single drawer to be open.
    */
   public void setBottomAllowMultipleOpenDrawers(final boolean allow) {
      bottom.setAllowMultipleOpenDrawers(allow);
   }

   /**
    * True to allow multiple drawers to be open within the left of
    * the pane. False to only allow a single drawer to be open.
    */
   public void setLeftAllowMultipleOpenDrawers(final boolean allow) {
      bottom.setAllowMultipleOpenDrawers(allow);
   }

   /**
    * Call to disable/enable a {@link DrawerNode}. Disabling
    * a {@link DrawerNode} will result in the {@link DrawerNode}
    * to be closed within the containing side, and the related
    * toolbar button being disabled.
    */
   public void setNodeDisable(final DrawerNode node,
                              final boolean disable) {
      setNodeDisable(top, getTopNodes(), node, disable);
      setNodeDisable(rightSide, getRightNodes(), node, disable);
      setNodeDisable(bottom, getBottomNodes(), node, disable);
      setNodeDisable(leftSide, getLeftNodes(), node, disable);
   }

   /**
    * Call to disable/enable a {@link DrawerNode}.
    */
   private void setNodeDisable(final AbstractSide side,
                               final List<DrawerNode> nodes,
                               final DrawerNode node,
                               final boolean disable) {
      nodes.stream()
         .filter(n -> n.equals(node))
         .findFirst()
         .ifPresent(n -> side.disable(node, disable));
   }

   /**
    * Helper function to load the default style sheet of DrawerPane.
    */
   public static void initializeDefaultUserAgentStylesheet() {
      StyleManager.getInstance()
         .addUserAgentStylesheet(DrawerPane.class.getResource("drawerpanefx.css").toExternalForm());
   }
}
