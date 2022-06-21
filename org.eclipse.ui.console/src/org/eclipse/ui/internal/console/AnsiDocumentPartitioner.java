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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

public class AnsiDocumentPartitioner implements IDocumentPartitioner {

	// store the last processed style
	private AnsiStyle style = AnsiStyle.DEFAULT;
	// match full escape sequence and incomplete escape sequence at the end
	private static final Pattern ESCAPE_SEQUENCE_PATTERN = Pattern
			.compile("\u001b(?:\\[[\\d;]*[A-HJKSTfimnsu]|(?:(?:\\[[\\d;]*)?\\z))"); //$NON-NLS-1$

	// the matcher used to find escape sequences
	private final Matcher matcher = ESCAPE_SEQUENCE_PATTERN.matcher("");//$NON-NLS-1$
	// the last incomplete escape sequence
	private final StringBuilder incompleteEscapeSequence = new StringBuilder();

	public static final String PARTITION_NAME = "ansi_console";//$NON-NLS-1$

	private boolean enable;
	private IDocument document;

	// the list of ANSI positions
	private final List<AnsiPosition> positions = new ArrayList<>();

	// Style list
	final List<StyleRange> styles = new ArrayList<>();

	// Store the last processed position index
	int indexHint = 0;

	public void updateEventStyles(LineStyleEvent event, Color foregroundColor, Color backgroundColor) {

		// no position means nothing to do
		if (positions.isEmpty() || event.lineText == null || event.lineText.isEmpty()) {
			return;
		}
		styles.clear();

		// keep existing styles if any
		if (event.styles != null) {
			Collections.addAll(styles, event.styles);
		}

		// filters all the positions that overlap with the current event line

		final var offset = event.lineOffset;
		final var length = event.lineText.length();

		final var rangeEnd = offset + length;
		var left = 0;
		var right = positions.size() - 1;

		var mid = indexHint <= right ? indexHint : (left + right) / 2;
		AnsiPosition position;

		// find the first overlapping position
		while (left <= right) {

			position = positions.get(mid);
			if (position.getOffset() > rangeEnd) {
				right = mid - 1;
			} else if (position.getOffset() + position.getLength() <= offset) {
				left = mid + 1;
			} else {
				left = mid;
				break;
			}
			mid = (left + right) / 2;
		}

		var index = left - 1;
		if (index >= 0) {
			position = positions.get(index);
			while (position.getOffset() + position.getLength() - 1 > offset) {
				index--;
				if (index < 0) {
					break;
				}
				position = positions.get(index);
			}
		}

		var found = false;

		// process positions that overlap with the current line
		while (++index < positions.size()) {
			position = positions.get(index);
			if (position.getOffset() >= rangeEnd) {
				break;
			}

			position.overrideStyleRange(styles, foregroundColor, backgroundColor);

			found = true;
		}

		// update event styles if found an overlapping position
		if (found) {
			indexHint = index;
			event.styles = styles.toArray(new StyleRange[styles.size()]);
		}
	}

	/**
	 *
	 * @param offset the text offset
	 * @param text   the new text
	 * @return true if the text is entirely processed (no partial escape sequence at
	 *         the end)
	 */
	private boolean doUpdate(int offset, CharSequence text) {

		matcher.reset(text);

		var start = 0;

		// find all escapes codes in the appended text and compute the new positions
		while (matcher.find()) {
			final var mstart = matcher.start();

			// add a position between two escape codes (or from the beginning to an escape
			// code)
			// add this position only if the style is of interest (different from
			// default)
			if (style != AnsiStyle.DEFAULT && mstart > start) {
				positions.add(new AnsiPosition.Style(start + offset, mstart - start, style));
			}
			final var group = matcher.group();

			// save the incomplete escape sequence if any
			if (matcher.hitEnd()) {

				incompleteEscapeSequence.setLength(0);
				incompleteEscapeSequence.append(group);
				return false;
			}

			// add a position to hide the escape code
			positions.add(new AnsiPosition.EscapeCode(mstart + offset, group.length()));

			// update the style
			style = style.apply(group);

			// update the start offset
			start = matcher.end();
		}

		// add a position between the last escape code (or from the beginning) and the
		// end of the appended text
		// add this position only if the attribute is of interest
		if (style != AnsiStyle.DEFAULT && text.length() > start) {
			positions.add(new AnsiPosition.Style(start + offset, text.length() - start, style));
		}
		return true;

	}

	private void update(int offset, String text) {

		if (text == null || text.isEmpty()) {
			return;
		}
		final var length = incompleteEscapeSequence.length();

		if (length == 0) {
			doUpdate(offset, text);
		} else if (doUpdate(offset - length, incompleteEscapeSequence.append(text))) {
			// reset
			incompleteEscapeSequence.setLength(0);
		}

	}

	private void doConnect() {

		style = AnsiStyle.DEFAULT;
		incompleteEscapeSequence.setLength(0);
		enable = true;

		positions.clear();
		// initialize the positions
		update(0, document.get());
	}

	@Override
	public void connect(IDocument newDocument) {
		this.document = newDocument;
		doConnect();
	}

	@Override
	public void disconnect() {
		enable = false;
		positions.clear();
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// ignore
	}

	@Override
	public boolean documentChanged(DocumentEvent event) {

		if (!AnsiConsolePreferences.interpretAnsiEscapeSequences()) {
			// disable this partitioner
			if (enable) {
				disconnect();
			}
		}
		// re-enable this partitioner
		else if (!enable) {
			doConnect();
		}

		else {
			// adapt existing positions (we are interested only by remove events)
			if (event.getOffset() == 0) {

				final var length = event.getLength();

				// remove all the starting positions
				positions.removeIf(p -> p.offset + p.length < length);

				if (length > 0) {

					// update remaining positions
					positions.parallelStream().forEach(p -> {

						if (p.offset > length) {
							// position after removed region
							// position: _________PPPPP
							// remove : xxxx
							// result : _____PPPPP
							p.offset -= length;
						} else {
							// position overlap with removed region
							// position: __PPPPP__
							// remove : xxxx
							// result : PPP__
							p.length -= length - p.offset;
							p.offset = 0;
						}
					});
				}
			}
			// handle new text
			update(event.getOffset(), event.getText());
		}
		return false;
	}

	@Override
	public String[] getLegalContentTypes() {
		return new String[0];
	}

	@Override
	public String getContentType(int offset) {
		return null;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		return new ITypedRegion[0];
	}

	@Override
	public ITypedRegion getPartition(int offset) {
		return null;
	}

}
