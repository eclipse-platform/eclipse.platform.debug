/*******************************************************************************
 * Copyright (c) 2022 daveluy and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yannick Daveluy - initial implementation
 *******************************************************************************/

package org.eclipse.ui.internal.console;

import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;

abstract class AnsiPosition extends Position {

	protected AnsiPosition(int offset, int length) {
		super(offset, length);
	}

	protected abstract void overrideStyleRange(StyleRange style, Color defaultForegroundColor,
			Color defaultBackgroundColor);

	public void overrideStyleRange(List<StyleRange> ranges, int rangeStart, int rangeEnd, Color foregroundColor,
			Color backgroundColor) {

		var start = Math.max(rangeStart, this.offset);
		var end = Math.min(this.offset + this.length, rangeEnd);

		var insertIndex = ranges.size();

		StyleRange newRange = null;

		for (var i = ranges.size() - 1; i >= 0; i--) {
			final var existingRange = ranges.get(i);
			final var existingStart = existingRange.start;
			final var existingEnd = existingStart + existingRange.length;

			// Find first position to insert where offset of new style is smaller then all
			// offsets before. This way the list is still sorted by offset after insert if
			// it was sorted before and it will not fail if list was not sorted.
			if (start <= existingStart) {
				insertIndex = i;
			}

			if (start == existingStart && end == existingEnd) {
				overrideStyleRange(existingRange, foregroundColor, backgroundColor);
				return;
			}
			if (start <= existingStart) { // new style starts before or with existing
				if (end < existingStart) {
					// new style lies before existing style. No overlapping.
					// new style: ++++_________
					// existing : ________=====
					// . result : ++++____=====
					// nothing to do
				} else if (end < existingEnd) {
					// new style overlaps start of existing.
					// new style: ++++++++_____
					// existing : _____========
					// . result : +++++xxx=====
					final var overlap = end - existingStart;
					if (overlap > 0) {
						final var clonedRange = (StyleRange) existingRange.clone();
						clonedRange.start += overlap;
						clonedRange.length -= overlap;
						ranges.add(i + 1, clonedRange);

						existingRange.length = overlap;
						overrideStyleRange(existingRange, foregroundColor, backgroundColor);

						end -= overlap;
					}
				} else {
					// new style completely overlaps existing.
					// new style: ___++++++++++
					// existing : ___======____
					// . result : ___xxxxxx++++
					// update the existing range and add the new one after

					overrideStyleRange(existingRange, foregroundColor, backgroundColor);
					start = existingEnd;
				}
			} else if (existingEnd < start) {
				// new style lies after existing style. No overlapping.
				// new style: _________++++
				// existing : =====________
				// . result : =====____++++
				// nothing to do
			} else if (end >= existingEnd) {
				// new style overlaps end of existing.
				// new style: _____++++++++
				// existing : ========_____
				// . result : =====xxx+++++
				final var overlap = existingEnd - start;
				if (overlap > 0) {
					final var clonedRange = (StyleRange) existingRange.clone();
					clonedRange.start = existingEnd;
					clonedRange.length = overlap;
					overrideStyleRange(clonedRange, foregroundColor, backgroundColor);
					ranges.add(i + 1, clonedRange);
				}
				existingRange.length -= overlap;
				start += overlap;
			} else {
				// new style lies inside existing style but not overrides all of it
				// (and does not touch first or last offset of existing)
				// new style: ____+++++____
				// existing : =============
				// . result : ====+++++====
				final var clonedRange = (StyleRange) existingRange.clone();
				existingRange.length = start - existingStart;
				clonedRange.start = end;
				clonedRange.length = existingEnd - end;
				ranges.add(i + 1, clonedRange);
				newRange = (StyleRange) existingRange.clone();
			}

		}
		if (end <= start) {
			return;
		}
		if (newRange == null) {
			newRange = new StyleRange();
		}
		newRange.start = start;
		newRange.length = end - start;
		overrideStyleRange(newRange, foregroundColor, backgroundColor);
		ranges.add(insertIndex, newRange);
	}

	public static class Style extends AnsiPosition {

		// Style for the current position
		protected final AnsiStyle style;

		/**
		 * Build a Style Position
		 *
		 * @param offset the position offset
		 * @param length the position length
		 * @param style  the style
		 */
		public Style(int offset, int length, AnsiStyle style) {
			super(offset, length);
			this.style = style;

		}

		/**
		 * Get the Style Range of the position
		 *
		 */
		@Override
		public void overrideStyleRange(StyleRange range, Color defaultForegroundColor, Color defaultBackgroundColor) {
			style.overrideStyleRange(range, defaultForegroundColor, defaultBackgroundColor);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Style) {
				final var s = (Style) other;
				return style.equals(s.style) && super.equals(other);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode() ^ style.hashCode();
		}

	}

	public static class EscapeCode extends AnsiPosition {
		private static final Font MONO_FONT = new Font(null, "Monospaced", 6, SWT.NORMAL); //$NON-NLS-1$
		private static final GlyphMetrics HIDE_CODE = new GlyphMetrics(0, 0, 0);

		/**
		 * Build an escape code position
		 *
		 * @param offset the position offset
		 * @param length the position length
		 */
		protected EscapeCode(int offset, int length) {
			super(offset, length);
		}

		/**
		 * Override the Style Range of the position
		 *
		 */
		@Override
		protected void overrideStyleRange(StyleRange range, Color defaultForegroundColor,
				Color defaultBackgroundColor) {
			// update the the Style according to current preferences
			if (AnsiConsolePreferences.showEscapeCodes()) {
				range.font = MONO_FONT; // Show the codes in small, monospaced font
			} else {
				range.metrics = HIDE_CODE; // Hide the codes
			}
		}
	}
}