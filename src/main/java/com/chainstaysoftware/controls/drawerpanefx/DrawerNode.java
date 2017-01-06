package com.chainstaysoftware.controls.drawerpanefx;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Base class for a node that provides content to include within a
 * drawer within the {@link DrawerPane}. The class also provides title text
 * and an icon to include within the show/hide button that is displayed within
 * a {@link DrawerPane} toolbar.
 */
public class DrawerNode extends Pane {
   private final Node contents;
   private final String title;
   private final Image icon;
   private final boolean canFloat;
   private final URL floatStyleSheet;
   private final List<Position> validPositions;

   private boolean isFloating;

   /**
    * Constructor. Defaults icon to null and canFloat to True.
    * @param contents Node to display when the drawer is open.
    * @param title Title to show on the drawer show/hide button.
    */
   public DrawerNode(final Node contents,
                     final String title) {
      this(contents, title, null, true, null, Collections.emptyList());
   }

   /**
    * Constructor
    * @param contents Node to display when the drawer is open.
    * @param title Title to show on the drawer show/hide button.
    * @param icon Icon to show on the drawer show/hide button. Can be null.
    * @param canFloat True if the contents can be detached from the {@link DrawerPane}
*                 and contained within its own window.
    * @param floatStyleSheet URL to style sheet to attach to scene when
    *                        the {@link DrawerNode} is floating. Null indicates none.
    * @param validPositions List of sides that this {@link DrawerNode} can
    *                       be positioned at. Empty list indicates that all
    */
   public DrawerNode(final Node contents,
                     final String title,
                     final Image icon,
                     final boolean canFloat,
                     final URL floatStyleSheet,
                     final List<Position> validPositions) {
      if (contents == null) {
         throw new IllegalArgumentException("contents must not be null");
      }

      if (title == null) {
         throw new IllegalArgumentException("title must not be null");
      }

      this.contents = contents;
      this.title = title;
      this.icon = icon;
      this.canFloat = canFloat;
      this.floatStyleSheet = floatStyleSheet;
      this.validPositions = Collections.unmodifiableList(validPositions);

      final VBox vBox = new VBox();
      vBox.setId("DrawerNodeVbox-" + title);
      vBox.getChildren().addAll(contents);

      getChildren().add(vBox);
   }

   /**
    * {@link Node} that this {@link DrawerNode} wraps
    */
   public Node getContents() {
      return contents;
   }

   /**
    * Title to use for show/hide button.
    */
   public String getTitle() {
      return title;
   }

   /**
    * Icon to use for show/hide button.
    */
   public Image getIcon() {
      return icon;
   }

   /**
    * True if this instance is currently floating (detached from the pane).
    */
   public boolean isFloating() {
      return isFloating;
   }

   /**
    * Sets the floating state for this instance.
    */
   public void setFloating(final boolean floating) {
      if (!canFloat && floating) {
         throw new IllegalArgumentException("Cannot set floating to true when canFloat is false!");
      }

      isFloating = floating;
   }

   /**
    * True if this instance can be detached from the owning {@link DrawerPane}.
    * False otherwise.
    */
   public boolean canFloat() {
      return canFloat;
   }

   public Optional<URL> getFloatStyleSheet() {
      return Optional.ofNullable(floatStyleSheet);
   }

   /**
    * A list of valid {@link Position}s for this instance.
    */
   public List<Position> getValidPositions() {
      return validPositions;
   }

   /**
    * True if this instance can be placed at the passed in {@link Position}.
    */
   public boolean isValidPosition(final Position position) {
      return position != null && (validPositions.isEmpty() || validPositions.contains(position));
   }
}
