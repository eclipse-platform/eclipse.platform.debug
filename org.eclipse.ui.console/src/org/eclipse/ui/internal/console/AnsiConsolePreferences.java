package org.eclipse.ui.internal.console;

import static org.eclipse.ui.console.IAnsiConsoleConstants.P_COLOR_PALETTE_NAME;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_CUSTOM_COLORS;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_INTERPRET_ANSI_ESCAPE_SEQUENCES;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_SHOW_ESCAPE_SEQUENCES;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.console.ConsolePlugin;

public class AnsiConsolePreferences {

	private static final IPreferenceStore PREF_STORE = ConsolePlugin.getDefault().getPreferenceStore();

	// Caching, for better performance.
	private static String getPreferredPalette = PREF_STORE.getString(P_COLOR_PALETTE_NAME);
	private static boolean showEscapeCodes = PREF_STORE.getBoolean(P_SHOW_ESCAPE_SEQUENCES);
	private static boolean isPrettyConsoleEnabled = PREF_STORE.getBoolean(P_INTERPRET_ANSI_ESCAPE_SEQUENCES);
	// private static boolean clipboardPutRtf =
	// PREF_STORE.getBoolean(PREF_PUT_RTF_IN_CLIPBOARD);

	static {
		PREF_STORE.addPropertyChangeListener(AnsiConsolePreferences::refresh);
	}

	private AnsiConsolePreferences() {
		// Utility class, should not be instantiated
	}

	/*
	 * public static boolean putRtfInClipboard() { return clipboardPutRtf; }
	 */

	// This is not cached, because it can change from both Preferences and the icon
	// on console
	public static boolean isPrettyConsoleEnabled() {
		return isPrettyConsoleEnabled;
	}

	public static void setPrettyConsoleEnabled(boolean enabled) {
		PREF_STORE.setValue(P_INTERPRET_ANSI_ESCAPE_SEQUENCES, enabled);
	}

	public static String getPreferredPalette() {
		return getPreferredPalette;
	}

	public static boolean showEscapeCodes() {
		return showEscapeCodes;
	}

	public static void refresh(PropertyChangeEvent evt) {

		if (P_COLOR_PALETTE_NAME.equals(evt.getProperty())) {
			getPreferredPalette = (String) evt.getNewValue();
		} else if (P_SHOW_ESCAPE_SEQUENCES.equals(evt.getProperty())) {
			showEscapeCodes = (boolean) evt.getNewValue();
		} else if (P_INTERPRET_ANSI_ESCAPE_SEQUENCES.equals(evt.getProperty())) {
			isPrettyConsoleEnabled = (boolean) evt.getNewValue();
			// } else if (PREF_PUT_RTF_IN_CLIPBOARD.equals(evt.getProperty())) {
			// clipboardPutRtf = (boolean) evt.getNewValue();
		} else {
			for (var i = 0; i < P_CUSTOM_COLORS.length; ++i) {
				if (P_CUSTOM_COLORS[i].equals(evt.getProperty())) {
					// update the current palette (do not replace)

					final var palette = AnsiConsoleColorPalette.getCurrentPalette();
					final var rgb = PreferenceConverter.getColor(PREF_STORE, evt.getProperty());
					palette[i].blue = rgb.blue;
					palette[i].green = rgb.green;
					palette[i].red = rgb.red;
					break;
				}
			}
		}

		AnsiLineStyleListener.getDefault().redrawViewers();
	}
}
