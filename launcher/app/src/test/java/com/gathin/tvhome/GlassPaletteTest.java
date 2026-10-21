package com.gathin.tvhome;
import org.junit.Test;
import static org.junit.Assert.*;
public class GlassPaletteTest {
 @Test public void hongKongDayBoundaries() {
  assertFalse(GlassPalette.lightAt(0));
  assertFalse(GlassPalette.lightAt(5));
  assertTrue(GlassPalette.lightAt(6));
  assertTrue(GlassPalette.lightAt(18));
  assertFalse(GlassPalette.lightAt(19));
  assertFalse(GlassPalette.lightAt(23));
 }
}
