package com.chainstaysoftware.drawerpanefx;

import javafx.scene.input.DataFormat;

/**
 * {@link DataFormat} used for dragging/dropping {@link DrawerNode} instances
 * between sides of the containing {@link DrawerPane}.
 */
public class DrawerDataFormat {
   public static final DataFormat CLIPBOARD_CONTENT_FORMAT
      = new DataFormat("com.chainstaysoftware.drawerpanefx.DrawerDataFormat");

   private DrawerDataFormat() {}
}
