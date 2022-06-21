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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;

public class AnsiLineStyleListener implements LineStyleListener {

	private static AnsiLineStyleListener instance = new AnsiLineStyleListener();

	private final List<StyledText> viewers = new ArrayList<>();

	public void install(StyledText viewer) {

		viewer.removeLineStyleListener(AnsiLineStyleListener.instance);
		viewer.addLineStyleListener(AnsiLineStyleListener.instance);
		viewers.add(viewer);
	}

	public static AnsiLineStyleListener getDefault() {
		return instance;
	}

	/**
	 * Redraw all visible viewers
	 */
	public void redrawViewers() {
		for (final var viewer : viewers) {
			if (!viewer.isDisposed() && viewer.isVisible()) {
				viewer.redraw();
			}
		}
	}

	private AnsiLineStyleListener() {
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {

		if (!AnsiConsolePreferences.interpretAnsiEscapeSequences() || !(event.widget instanceof StyledText)) {
			return;
		}

		final var text = (StyledText) event.widget;

		final var document = AnsiConsolePageParticipant.getDocument(text);

		if (!(document instanceof IDocumentExtension3)) {
			return;
		}

		final var docExt = (IDocumentExtension3) document;

		var partitioner = (AnsiDocumentPartitioner) docExt
				.getDocumentPartitioner(AnsiDocumentPartitioner.PARTITION_NAME);

		// Install the AnsiDocumentPartitioner if not already installed
		if (partitioner == null) {
			partitioner = new AnsiDocumentPartitioner();
			partitioner.connect(document);
			docExt.setDocumentPartitioner(AnsiDocumentPartitioner.PARTITION_NAME, partitioner);
		}

		// update event styles
		partitioner.updateEventStyles(event, text.getForeground(), text.getBackground());

	}

}
