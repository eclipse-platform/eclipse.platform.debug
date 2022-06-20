package org.eclipse.ui.internal.console;

import static org.eclipse.ui.console.IAnsiConsoleConstants.P_COLOR_PALETTE_NAME;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_CUSTOM_COLORS;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_INTERPRET_ANSI_ESCAPE_SEQUENCES;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_SHOW_ESCAPE_SEQUENCES;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.ui.console.ConsolePlugin;

public class AnsiConsolePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final var prefs = ConsolePlugin.getDefault().getPreferenceStore();
		prefs.setDefault(P_INTERPRET_ANSI_ESCAPE_SEQUENCES, true);
		prefs.setDefault(P_SHOW_ESCAPE_SEQUENCES, false);
		prefs.setDefault(P_COLOR_PALETTE_NAME, AnsiConsoleColorPalette.getBestPaletteForOS());
		// prefs.setDefault(PREF_PUT_RTF_IN_CLIPBOARD, true);

		final var palette = AnsiConsoleColorPalette.getCurrentPalette();
		for (var i = 0; i < P_CUSTOM_COLORS.length; ++i) {
			PreferenceConverter.setDefault(prefs, P_CUSTOM_COLORS[i], palette[i]);
		}

	}

}
