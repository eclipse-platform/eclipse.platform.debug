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

import static org.eclipse.ui.internal.console.IAnsiConsoleConstants.P_COLOR_PALETTE_NAME;
import static org.eclipse.ui.internal.console.IAnsiConsoleConstants.P_CUSTOM_COLORS;
import static org.eclipse.ui.internal.console.IAnsiConsoleConstants.P_INTERPRET_ANSI_ESCAPE_SEQUENCES;
import static org.eclipse.ui.internal.console.IAnsiConsoleConstants.P_SHOW_ESCAPE_SEQUENCES;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.ui.console.ConsolePlugin;

/**
 * Default preference values for ANSI Console
 *
 * @since 4.25
 */
public class AnsiConsolePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final var prefs = ConsolePlugin.getDefault().getPreferenceStore();
		prefs.setDefault(P_INTERPRET_ANSI_ESCAPE_SEQUENCES, true);
		prefs.setDefault(P_SHOW_ESCAPE_SEQUENCES, false);
		prefs.setDefault(P_COLOR_PALETTE_NAME, AnsiConsoleColorPalette.getBestPaletteForOS());

		final var palette = AnsiConsoleColorPalette.getCurrentPalette();
		for (var i = 0; i < P_CUSTOM_COLORS.length; ++i) {
			PreferenceConverter.setDefault(prefs, P_CUSTOM_COLORS[i], palette[i]);
		}

	}

}
