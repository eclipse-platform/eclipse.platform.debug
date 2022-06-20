package org.eclipse.ui.internal.console;


import java.util.Iterator;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class AnsiStyleAttribute {

	private static final int UNDERLINE_SIMPLE = 1 << 27;
	private static final int UNDERLINE_DOUBLE = 1 << 28;
	private static final int STRIKETHROUGH = 1 << 29;
	private static final int INVERT = 1 << 30;
	private static final int CONCEAL = 1 << 31;
	private static final int FONT_STYLE_MASK = SWT.ITALIC | SWT.BOLD | SWT.NORMAL;
	private static final int UNDERLINE_STYLE_MASK = UNDERLINE_SIMPLE | UNDERLINE_DOUBLE;

	private static final int COMMAND_ATTR_RESET = 0; // Reset / Normal (all attributes off)
	private static final int COMMAND_ATTR_INTENSITY_BRIGHT = 1; // Bright (increased intensity) or Bold
	private static final int COMMAND_ATTR_INTENSITY_FAINT = 2; // Faint (decreased intensity) (not widely supported)
	private static final int COMMAND_ATTR_ITALIC = 3; // Italic: on not widely supported. Sometimes treated as inverse.
	private static final int COMMAND_ATTR_UNDERLINE = 4; // Underline: Single
	// private static final int COMMAND_ATTR_BLINK_SLOW = 5; // Blink: Slow (less
	// than 150 per minute)
	// private static final int COMMAND_ATTR_BLINK_FAST = 6; // Blink: Rapid (MS-DOS
	// ANSI.SYS; 150 per minute or more; not
	// widely supported)
	private static final int COMMAND_ATTR_NEGATIVE_ON = 7; // Image: Negative (inverse or reverse; swap foreground and
															// background)
	private static final int COMMAND_ATTR_CONCEAL_ON = 8; // Conceal (not widely supported)
	private static final int COMMAND_ATTR_CROSSOUT_ON = 9; // Crossed-out (Characters legible, but marked for deletion.
															// Not widely supported.)
	private static final int COMMAND_ATTR_UNDERLINE_DOUBLE = 21; // Bright/Bold: off or Underline: Double (bold off not
																	// widely supported, double underline hardly ever)
	private static final int COMMAND_ATTR_INTENSITY_NORMAL = 22; // Normal color or intensity (neither bright, bold nor
																	// faint)
	private static final int COMMAND_ATTR_ITALIC_OFF = 23; // Not italic, not Fraktur
	private static final int COMMAND_ATTR_UNDERLINE_OFF = 24; // Underline: None (not singly or doubly underlined)
	// private static final int COMMAND_ATTR_BLINK_OFF = 25; // Blink: off
	private static final int COMMAND_ATTR_NEGATIVE_OFF = 27; // Image: Positive
	private static final int COMMAND_ATTR_CONCEAL_OFF = 28; // Reveal (conceal off)
	private static final int COMMAND_ATTR_CROSSOUT_OFF = 29; // Not crossed out

	// Extended colors. Next arguments are 5;<index_0_255> or
	// 2;<red_0_255>;<green_0_255>;<blue_0_255>
	private static final int COMMAND_HICOLOR_FOREGROUND = 38; // Set text color
	private static final int COMMAND_HICOLOR_BACKGROUND = 48; // Set background color

	private static final int COMMAND_COLOR_FOREGROUND_RESET = 39; // Default text color
	private static final int COMMAND_COLOR_BACKGROUND_RESET = 49; // Default background color

	private static final int COMMAND_COLOR_FOREGROUND_FIRST = 30; // First text color
	private static final int COMMAND_COLOR_FOREGROUND_LAST = 37; // Last text color
	private static final int COMMAND_COLOR_BACKGROUND_FIRST = 40; // First background text color
	private static final int COMMAND_COLOR_BACKGROUND_LAST = 47; // Last background text color

	private static final int COMMAND_ATTR_FRAMED_ON = 51; // Framed
	private static final int COMMAND_ATTR_FRAMED_OFF = 54; // Not framed or encircled

	private static final int COMMAND_HICOLOR_FOREGROUND_FIRST = 90; // First text color
	private static final int COMMAND_HICOLOR_FOREGROUND_LAST = 97; // Last text color
	private static final int COMMAND_HICOLOR_BACKGROUND_FIRST = 100; // First background text color
	private static final int COMMAND_HICOLOR_BACKGROUND_LAST = 107; // Last background text color

	private static final int COMMAND_COLOR_INTENSITY_DELTA = 8; // Last background text color

	private static final char ESCAPE_SGR = 'm';
	public static final AnsiStyleAttribute DEFAULT = new AnsiStyleAttribute();

	// If you change any of these also update reset()
	private RGB background;
	private RGB foreground;
	private int style;

	private AnsiStyleAttribute() {
		reset();
	}

	private AnsiStyleAttribute(AnsiStyleAttribute other) {
		this.background = other.background;
		this.foreground = other.foreground;
		this.style = other.style;
	}

	private void reset() {
		background = null;
		foreground = null;
		style = SWT.NORMAL;
	}

	private boolean isDefault() {
		return foreground == null && style == SWT.NORMAL && background == null;

	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		if (background != null) {
			result.append("Bg" + background); //$NON-NLS-1$
		}
		if (foreground != null) {
			result.append("Fg" + foreground); //$NON-NLS-1$
		}

		if ((style & UNDERLINE_SIMPLE) != 0) {
			result.append("_");//$NON-NLS-1$
		}
		if ((style & SWT.BOLD) != 0) {
			result.append("B");//$NON-NLS-1$
		}
		if ((style & SWT.ITALIC) != 0) {
			result.append("I");//$NON-NLS-1$
		}

		if ((style & INVERT) != 0) {
			result.append("!");//$NON-NLS-1$
		}
		if ((style & CONCEAL) != 0) {
			result.append("H");//$NON-NLS-1$
		}

		if ((style & STRIKETHROUGH) != 0) {
			result.append("-");//$NON-NLS-1$
		}

		if ((style & SWT.BORDER) != 0) {
			result.append("[]");//$NON-NLS-1$
		}

		return result.toString();
	}

	// This function maps from the current attributes as "described" by escape
	// sequences to real,
	// Eclipse console specific attributes (resolving color palette, default colors,
	// etc.)
	public static void updateRangeStyle(StyleRange range, final AnsiStyleAttribute attribute) {

		// update the foreground color
		if (attribute.foreground != null) {
			range.foreground = AnsiConsoleColorPalette.getColor(attribute.foreground);
		}

		// update the background color
		if (attribute.background != null) {
			range.background = AnsiConsoleColorPalette.getColor(attribute.background);
		}

		if ((attribute.style & INVERT) != 0) {
			// swap background/foreground
			final Color tmp = range.background;
			range.background = range.foreground;
			range.foreground = tmp;
		}

		if ((attribute.style & CONCEAL) != 0) {
			range.foreground = range.background;
		}

		range.font = null;
		range.fontStyle = attribute.style & FONT_STYLE_MASK;

		// Prepare the rest of the attributes
		if ((attribute.style & UNDERLINE_STYLE_MASK) != 0) {
			range.underline = true;
			range.underlineColor = range.foreground;
			range.underlineStyle = (attribute.style & UNDERLINE_DOUBLE) != 0 ? SWT.UNDERLINE_DOUBLE
					: SWT.UNDERLINE_SINGLE;
		}

		range.strikeout = (attribute.style & STRIKETHROUGH) != 0;
		if (range.strikeout) {
			range.strikeoutColor = range.foreground;
		}

		if ((attribute.style & SWT.BORDER) != 0) {
			range.borderStyle = SWT.BORDER_SOLID;
			range.borderColor = range.foreground;
		}
	}

	/**
	 * Apply an ansi escape code to the current attribute
	 *
	 * @param ansiCode the ansi code
	 * @return the resulting attributes
	 */
	public AnsiStyleAttribute apply(String ansiCode) {
		final char code = ansiCode.charAt(ansiCode.length() - 1);
		if (code == ESCAPE_SGR) {

			final AnsiStyleAttribute newAttribute = new AnsiStyleAttribute(this);

			// Select Graphic Rendition (SGR) escape sequence
			newAttribute.interpretCommand(ansiCode.substring(2, ansiCode.length() - 1));
			if (newAttribute.isDefault()) {
				return AnsiStyleAttribute.DEFAULT;
			}

			return newAttribute;
		}
		return this;
	}

	private void interpretCommand(String text) {

		if (text.isEmpty()) {
			reset();
			return;
		}

		final Iterator<Integer> iter = Stream.of(text.split(";")).map(Integer::parseInt).iterator(); //$NON-NLS-1$
		while (iter.hasNext()) {
			final int nCmd = iter.next();
			switch (nCmd) {
			case COMMAND_ATTR_RESET:
				reset();
				break;

			case COMMAND_ATTR_INTENSITY_BRIGHT:
				style |= SWT.BOLD;
				break;
			case COMMAND_ATTR_INTENSITY_FAINT: // Intentional fallthrough
			case COMMAND_ATTR_INTENSITY_NORMAL:
				style &= ~SWT.BOLD;
				break;

			case COMMAND_ATTR_ITALIC:
				style |= SWT.ITALIC;
				break;
			case COMMAND_ATTR_ITALIC_OFF:
				style &= ~SWT.ITALIC;
				break;

			case COMMAND_ATTR_UNDERLINE:
				style |= UNDERLINE_SIMPLE;
				break;
			case COMMAND_ATTR_UNDERLINE_DOUBLE:
				style |= UNDERLINE_DOUBLE;
				break;
			case COMMAND_ATTR_UNDERLINE_OFF:
				style &= ~UNDERLINE_DOUBLE;
				break;

			case COMMAND_ATTR_CROSSOUT_ON:
				style |= STRIKETHROUGH;
				break;
			case COMMAND_ATTR_CROSSOUT_OFF:
				style &= ~STRIKETHROUGH;
				break;

			case COMMAND_ATTR_NEGATIVE_ON:
				style |= INVERT;
				break;
			case COMMAND_ATTR_NEGATIVE_OFF:
				style &= ~INVERT;
				break;

			case COMMAND_ATTR_CONCEAL_ON:
				style |= CONCEAL;
				break;
			case COMMAND_ATTR_CONCEAL_OFF:
				style &= ~CONCEAL;
				break;

			case COMMAND_ATTR_FRAMED_ON:
				style |= SWT.BORDER;
				break;
			case COMMAND_ATTR_FRAMED_OFF:
				style &= ~SWT.BORDER;
				break;

			case COMMAND_COLOR_FOREGROUND_RESET:
				foreground = null;
				break;
			case COMMAND_COLOR_BACKGROUND_RESET:
				background = null;
				break;

			case COMMAND_HICOLOR_FOREGROUND:
			case COMMAND_HICOLOR_BACKGROUND: // {esc}[48;5;{color}m
				int color = -1;
				final int nMustBe2or5 = iter.hasNext() ? iter.next() : -1;
				if (nMustBe2or5 == 5) { // 256 colors
					color = iter.hasNext() ? iter.next() : -1;
					if (!AnsiConsoleColorPalette.isValidIndex(color)) {
						color = -1;
					}
				} else if (nMustBe2or5 == 2) { // rgb colors
					final int r = iter.hasNext() ? iter.next() : -1;
					final int g = iter.hasNext() ? iter.next() : -1;
					final int b = iter.hasNext() ? iter.next() : -1;
					color = AnsiConsoleColorPalette.hackRgb(r, g, b);
				}
				if (color != -1) {
					if (nCmd == COMMAND_HICOLOR_FOREGROUND) {
						foreground = AnsiConsoleColorPalette.getColor(color);
					} else {
						background = AnsiConsoleColorPalette.getColor(color);
					}
				}
				break;

			case -1:
				break; // do nothing

			default:
				if (nCmd >= COMMAND_COLOR_FOREGROUND_FIRST && nCmd <= COMMAND_COLOR_FOREGROUND_LAST) {
					// text color
					foreground = AnsiConsoleColorPalette.getColor(nCmd - COMMAND_COLOR_FOREGROUND_FIRST);
				} else if (nCmd >= COMMAND_COLOR_BACKGROUND_FIRST && nCmd <= COMMAND_COLOR_BACKGROUND_LAST) {
					// background color
					background = AnsiConsoleColorPalette.getColor(nCmd - COMMAND_COLOR_BACKGROUND_FIRST);
				} else if (nCmd >= COMMAND_HICOLOR_FOREGROUND_FIRST && nCmd <= COMMAND_HICOLOR_FOREGROUND_LAST) {
					// text color
					foreground = AnsiConsoleColorPalette
							.getColor(nCmd - COMMAND_HICOLOR_FOREGROUND_FIRST + COMMAND_COLOR_INTENSITY_DELTA);
				} else if (nCmd >= COMMAND_HICOLOR_BACKGROUND_FIRST && nCmd <= COMMAND_HICOLOR_BACKGROUND_LAST) {
					// background color
					background = AnsiConsoleColorPalette
							.getColor(nCmd - COMMAND_HICOLOR_BACKGROUND_FIRST + COMMAND_COLOR_INTENSITY_DELTA);
				}
			}
		}
	}
}
