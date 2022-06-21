/*******************************************************************************
 * Copyright (c) 2022 Mihai Nita and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Mihai Nita - initial implementation
 *     Yannick Daveluy - eclipse integration
 *******************************************************************************/
package org.eclipse.ui.internal.console;

import java.util.Iterator;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class AnsiStyle {

	private static final int UNDERLINE_SIMPLE = 1 << 27;
	private static final int UNDERLINE_DOUBLE = 1 << 28;
	private static final int STRIKETHROUGH = 1 << 29;
	private static final int INVERT = 1 << 30;
	private static final int CONCEAL = 1 << 31;
	private static final int FONT_STYLE_MASK = SWT.ITALIC | SWT.BOLD | SWT.NORMAL;
	private static final int UNDERLINE_STYLE_MASK = UNDERLINE_SIMPLE | UNDERLINE_DOUBLE;

	private static final char ESCAPE_SGR = 'm';
	public static final AnsiStyle DEFAULT = new AnsiStyle();

	// If you change any of these also update reset()
	private RGB background;
	private RGB foreground;
	private int style;

	private AnsiStyle() {
		reset();
	}

	private AnsiStyle(AnsiStyle other) {
		this.background = other.background;
		this.foreground = other.foreground;
		this.style = other.style;
	}

	public void reset() {
		background = null;
		foreground = null;
		style = SWT.NORMAL;
	}

	private boolean isDefault() {
		return foreground == null && style == SWT.NORMAL && background == null;

	}

	@Override
	public String toString() {
		final var result = new StringBuilder();
		if (background != null) {
			result.append("Bg" + background); //$NON-NLS-1$
		}
		if (foreground != null) {
			result.append("Fg" + foreground); //$NON-NLS-1$
		}

		if ((style & UNDERLINE_STYLE_MASK) != 0) {
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

	/**
	 * Convert this ANSI Style to a StyleRange
	 *
	 * @param offset          the offset
	 * @param length          the length
	 * @param foregroundColor the default foreground color
	 * @param backgroundColor the default background color
	 * @return the StyleRange
	 */
	public StyleRange toStyleRange(int offset, int length, Color foregroundColor, Color backgroundColor) {

		final var range = new StyleRange(offset, length, foregroundColor, backgroundColor);
		// update the foreground color
		if (foreground != null) {
			range.foreground = AnsiConsoleColorPalette.getColor(foreground);
		}

		// update the background color
		if (background != null) {
			range.background = AnsiConsoleColorPalette.getColor(background);
		}

		if ((style & INVERT) != 0) {
			// swap background/foreground
			final var tmp = range.background;
			range.background = range.foreground;
			range.foreground = tmp;
		}

		if ((style & CONCEAL) != 0) {
			range.foreground = range.background;
		}

		range.font = null;
		range.fontStyle = style & FONT_STYLE_MASK;

		// Prepare the rest of the attributes
		if ((style & UNDERLINE_STYLE_MASK) != 0) {
			range.underline = true;
			range.underlineColor = range.foreground;
			range.underlineStyle = (style & UNDERLINE_DOUBLE) != 0 ? SWT.UNDERLINE_DOUBLE : SWT.UNDERLINE_SINGLE;
		}

		range.strikeout = (style & STRIKETHROUGH) != 0;
		if (range.strikeout) {
			range.strikeoutColor = range.foreground;
		}

		if ((style & SWT.BORDER) != 0) {
			range.borderStyle = SWT.BORDER_SOLID;
			range.borderColor = range.foreground;
		}
		return range;
	}

	/**
	 * Apply an ansi escape code to the current attribute
	 *
	 * @param ansiCode the ansi code
	 * @return the resulting attributes
	 */
	public AnsiStyle apply(String ansiCode) {
		final var code = ansiCode.charAt(ansiCode.length() - 1);
		if (code == ESCAPE_SGR) {

			final var newAttribute = new AnsiStyle(this);

			// Select Graphic Rendition (SGR) escape sequence
			newAttribute.interpretCommand(ansiCode.substring(2, ansiCode.length() - 1));
			if (newAttribute.isDefault()) {
				return AnsiStyle.DEFAULT;
			}

			return newAttribute;
		}
		return this;
	}

	private RGB getColor(Iterator<Integer> iter) {
		if (iter.hasNext()) {
			switch (iter.next()) {
			case 5: // 256 color
				if (iter.hasNext()) {
					return AnsiConsoleColorPalette.getSafeColor(iter.next());
				}
				break;
			case 2: // rgb color
				if (iter.hasNext()) {
					final var r = iter.next();
					if (iter.hasNext()) {
						final var g = iter.next();
						if (iter.hasNext()) {
							return AnsiConsoleColorPalette.getRgbColor(r, g, iter.next());
						}
					}
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	private void interpretCommand(String text) {

		if (text.isEmpty()) {
			reset();
			return;
		}

		final var iter = Stream.of(text.split(";")).map(Integer::parseInt).iterator(); //$NON-NLS-1$
		while (iter.hasNext()) {
			final int nCmd = iter.next();
			switch (nCmd) {
			case 0: // RESET
				reset();
				break;

			case 1: // BOLD
				style |= SWT.BOLD;
				break;
			case 2: // FAINT INTENSITY
			case 22: // NORMAL INTENSITY
				style &= ~SWT.BOLD;
				break;

			case 3: // ITALIC
				style |= SWT.ITALIC;
				break;
			case 23: // ITALIC OFF
				style &= ~SWT.ITALIC;
				break;

			case 4: // UNDERLINE
				style |= UNDERLINE_SIMPLE;
				break;
			case 21: // UNDERLINE DOUBLE
				style |= UNDERLINE_DOUBLE;
				break;
			case 24: // UNDERLINE OFF
				style &= ~UNDERLINE_STYLE_MASK;
				break;

			case 9: // STRIKE THROUGH
				style |= STRIKETHROUGH;
				break;
			case 29: // STRIKE THROUGH OFF
				style &= ~STRIKETHROUGH;
				break;

			case 7: // INVERT
				style |= INVERT;
				break;
			case 27: // INVERT OFF
				style &= ~INVERT;
				break;

			case 8: // CONCEAL
				style |= CONCEAL;
				break;
			case 28: // CONCEAL OFF
				style &= ~CONCEAL;
				break;

			case 51: // BORDER
				style |= SWT.BORDER;
				break;
			case 54: // BORDER OFF
				style &= ~SWT.BORDER;
				break;

			case 39: // RESET FOREGROUND
				foreground = null;
				break;
			case 49: // RESET BACKGROUND
				background = null;
				break;

			case 38: // FOREGROUND COLOR
			{
				final var newColor = getColor(iter);
				if (newColor != null) {
					foreground = newColor;
				}
				break;
			}
			case 48: // BACKGROUND COLOR
			{
				final var newColor = getColor(iter);
				if (newColor != null) {
					background = newColor;
				}
				break;
			}

			// DEFAULT FOREGROUND COLORS
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
				foreground = AnsiConsoleColorPalette.getColor(nCmd - 30);
				break;

			// DEFAULT BACKGROUND COLORS
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
				background = AnsiConsoleColorPalette.getColor(nCmd - 40);
				break;

			// DEFAULT FOREGROUND BRIGHT COLORS
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
				foreground = AnsiConsoleColorPalette.getColor(nCmd - 90 + 8);
				break;

			// DEFAULT BACKGROUND BRIGHT COLORS
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
				background = AnsiConsoleColorPalette.getColor(nCmd - 100 + 8);
				break;

			default:
				// ignore
				break;
			}
		}
	}
}
