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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Ansi Console Color Palette
 *
 * @since 4.25
 * @noextend This interface is not intended to be extended by clients.
 */
public class AnsiConsoleColorPalette {
	public static final String PALETTE_VGA = "paletteVGA"; //$NON-NLS-1$
	public static final String PALETTE_WINXP = "paletteXP";//$NON-NLS-1$
	public static final String PALETTE_WIN10 = "paletteWin10";//$NON-NLS-1$
	public static final String PALETTE_MAC = "paletteMac";//$NON-NLS-1$
	public static final String PALETTE_PUTTY = "palettePuTTY";//$NON-NLS-1$
	public static final String PALETTE_XTERM = "paletteXTerm";//$NON-NLS-1$
	public static final String PALETTE_MIRC = "paletteMirc";//$NON-NLS-1$
	public static final String PALETTE_UBUNTU = "paletteUbuntu";//$NON-NLS-1$
	public static final String PALETTE_TANGO = "paletteTango";//$NON-NLS-1$
	public static final String PALETTE_RXVT = "paletteRxvt";//$NON-NLS-1$
	public static final String PALETTE_CUSTOM = "paletteCustom";//$NON-NLS-1$

	private AnsiConsoleColorPalette() {
		// Utility class, should not be instantiated
	}

	// From Wikipedia, https://en.wikipedia.org/wiki/ANSI_escape_code
	private static final RGB[] paletteVGA = { new RGB(0, 0, 0), // black
			new RGB(170, 0, 0), // red
			new RGB(0, 170, 0), // green
			new RGB(170, 85, 0), // brown/yellow
			new RGB(0, 0, 170), // blue
			new RGB(170, 0, 170), // magenta
			new RGB(0, 170, 170), // cyan
			new RGB(170, 170, 170), // gray
			new RGB(85, 85, 85), // dark gray
			new RGB(255, 85, 85), // bright red
			new RGB(85, 255, 85), // bright green
			new RGB(255, 255, 85), // yellow
			new RGB(85, 85, 255), // bright blue
			new RGB(255, 85, 255), // bright magenta
			new RGB(85, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};
	private static final RGB[] paletteXP = { new RGB(0, 0, 0), // black
			new RGB(128, 0, 0), // red
			new RGB(0, 128, 0), // green
			new RGB(128, 128, 0), // brown/yellow
			new RGB(0, 0, 128), // blue
			new RGB(128, 0, 128), // magenta
			new RGB(0, 128, 128), // cyan
			new RGB(192, 192, 192), // gray
			new RGB(128, 128, 128), // dark gray
			new RGB(255, 0, 0), // bright red
			new RGB(0, 255, 0), // bright green
			new RGB(255, 255, 0), // yellow
			new RGB(0, 0, 255), // bright blue
			new RGB(255, 0, 255), // bright magenta
			new RGB(0, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};
	private static final RGB[] paletteWin10 = { new RGB(12, 12, 12), // black
			new RGB(197, 15, 31), // red
			new RGB(19, 161, 14), // green
			new RGB(193, 156, 0), // brown/yellow
			new RGB(0, 55, 218), // blue
			new RGB(136, 23, 152), // magenta
			new RGB(58, 150, 221), // cyan
			new RGB(204, 204, 204), // gray
			new RGB(118, 118, 118), // dark gray
			new RGB(231, 72, 86), // bright red
			new RGB(22, 198, 12), // bright green
			new RGB(249, 241, 165), // yellow
			new RGB(59, 120, 255), // bright blue
			new RGB(180, 0, 158), // bright magenta
			new RGB(97, 214, 214), // bright cyan
			new RGB(242, 242, 242) // white
	};
	private static final RGB[] paletteMac = { new RGB(0, 0, 0), // black
			new RGB(194, 54, 33), // red
			new RGB(37, 188, 36), // green
			new RGB(173, 173, 39), // brown/yellow
			new RGB(73, 46, 225), // blue
			new RGB(211, 56, 211), // magenta
			new RGB(51, 187, 200), // cyan
			new RGB(203, 204, 205), // gray
			new RGB(129, 131, 131), // dark gray
			new RGB(252, 57, 31), // bright red
			new RGB(49, 231, 34), // bright green
			new RGB(234, 236, 35), // yellow
			new RGB(88, 51, 255), // bright blue
			new RGB(249, 53, 248), // bright magenta
			new RGB(20, 240, 240), // bright cyan
			new RGB(233, 235, 235) // white
	};
	private static final RGB[] palettePuTTY = { new RGB(0, 0, 0), // black
			new RGB(187, 0, 0), // red
			new RGB(0, 187, 0), // green
			new RGB(187, 187, 0), // brown/yellow
			new RGB(0, 0, 187), // blue
			new RGB(187, 0, 187), // magenta
			new RGB(0, 187, 187), // cyan
			new RGB(187, 187, 187), // gray
			new RGB(85, 85, 85), // dark gray
			new RGB(255, 85, 85), // bright red
			new RGB(85, 255, 85), // bright green
			new RGB(255, 255, 85), // yellow
			new RGB(85, 85, 255), // bright blue
			new RGB(255, 85, 255), // bright magenta
			new RGB(85, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};
	private static final RGB[] paletteXTerm = { new RGB(0, 0, 0), // black
			new RGB(205, 0, 0), // red
			new RGB(0, 205, 0), // green
			new RGB(205, 205, 0), // brown/yellow
			new RGB(0, 0, 238), // blue
			new RGB(205, 0, 205), // magenta
			new RGB(0, 205, 205), // cyan
			new RGB(229, 229, 229), // gray
			new RGB(127, 127, 127), // dark gray
			new RGB(255, 0, 0), // bright red
			new RGB(0, 255, 0), // bright green
			new RGB(255, 255, 0), // yellow
			new RGB(92, 92, 255), // bright blue
			new RGB(255, 0, 255), // bright magenta
			new RGB(0, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};
	private static final RGB[] paletteMirc = { new RGB(0, 0, 0), // black
			new RGB(127, 0, 0), // red
			new RGB(0, 147, 0), // green
			new RGB(252, 127, 0), // brown/yellow
			new RGB(0, 0, 127), // blue
			new RGB(156, 0, 156), // magenta
			new RGB(0, 147, 147), // cyan
			new RGB(210, 210, 210), // gray
			new RGB(127, 127, 127), // dark gray
			new RGB(255, 0, 0), // bright red
			new RGB(0, 252, 0), // bright green
			new RGB(255, 255, 0), // yellow
			new RGB(0, 0, 252), // bright blue
			new RGB(255, 0, 255), // bright magenta
			new RGB(0, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};
	private static final RGB[] paletteUbuntu = { new RGB(1, 1, 1), // black
			new RGB(222, 56, 43), // red
			new RGB(57, 181, 74), // green
			new RGB(255, 199, 6), // brown/yellow
			new RGB(0, 111, 184), // blue
			new RGB(118, 38, 113), // magenta
			new RGB(44, 181, 233), // cyan
			new RGB(204, 204, 204), // gray
			new RGB(128, 128, 128), // dark gray
			new RGB(255, 0, 0), // bright red
			new RGB(0, 255, 0), // bright green
			new RGB(255, 255, 0), // yellow
			new RGB(0, 0, 255), // bright blue
			new RGB(255, 0, 255), // bright magenta
			new RGB(0, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};

	private static final RGB[] paletteTango = { new RGB(0, 0, 0), // black
			new RGB(204, 0, 0), // red
			new RGB(78, 154, 6), // green
			new RGB(196, 160, 0), // brown/yellow
			new RGB(52, 101, 164), // blue
			new RGB(117, 80, 123), // magenta
			new RGB(6, 152, 154), // cyan
			new RGB(211, 215, 207), // gray
			new RGB(85, 87, 83), // dark gray
			new RGB(239, 41, 41), // bright red
			new RGB(138, 226, 52), // bright green
			new RGB(252, 233, 79), // yellow
			new RGB(114, 159, 207), // bright blue
			new RGB(173, 127, 168), // bright magenta
			new RGB(52, 226, 226), // bright cyan
			new RGB(238, 238, 236) // white
	};

	private static final RGB[] paletteRxvt = { new RGB(0, 0, 0), // black
			new RGB(205, 0, 0), // red
			new RGB(0, 205, 0), // green
			new RGB(205, 205, 0), // brown/yellow
			new RGB(0, 0, 205), // blue
			new RGB(205, 0, 205), // magenta
			new RGB(0, 205, 205), // cyan
			new RGB(250, 235, 215), // gray
			new RGB(64, 64, 64), // dark gray
			new RGB(255, 0, 0), // bright red
			new RGB(0, 255, 0), // bright green
			new RGB(255, 255, 0), // yellow
			new RGB(0, 0, 255), // bright blue
			new RGB(255, 0, 255), // bright magenta
			new RGB(0, 255, 255), // bright cyan
			new RGB(255, 255, 255) // white
	};

	private static final Map<String, RGB[]> KNOWN_PALETTES = new HashMap<>();
	static {
		KNOWN_PALETTES.put(PALETTE_MAC, paletteMac);
		KNOWN_PALETTES.put(PALETTE_VGA, paletteVGA);
		KNOWN_PALETTES.put(PALETTE_WINXP, paletteXP);
		KNOWN_PALETTES.put(PALETTE_WIN10, paletteWin10);
		KNOWN_PALETTES.put(PALETTE_XTERM, paletteXTerm);
		KNOWN_PALETTES.put(PALETTE_PUTTY, palettePuTTY);
		KNOWN_PALETTES.put(PALETTE_MIRC, paletteMirc);
		KNOWN_PALETTES.put(PALETTE_UBUNTU, paletteUbuntu);
		KNOWN_PALETTES.put(PALETTE_TANGO, paletteTango);
		KNOWN_PALETTES.put(PALETTE_RXVT, paletteRxvt);
	}
	private static final String PALETTE_NAME = getBestPaletteForOS();
	private static RGB[] palette;

	public static RGB[] getCurrentPalette() {
		return palette;
	}

	public static RGB[] getPalette(Object name) {
		return KNOWN_PALETTES.get(name);

	}

	private static final HashMap<RGB, Color> CACHE = new HashMap<>();

	public static synchronized Color getColor(RGB rgb) {
		return CACHE.computeIfAbsent(rgb, color -> new Color(null, color));
	}

	/**
	 * Pre-calculate the palette table
	 */
	static {

		var index = 0;
		palette = new RGB[256];
		final var defaultPalette = KNOWN_PALETTES.get(PALETTE_NAME);
		for (; index < defaultPalette.length; ++index) {
			palette[index] = new RGB(defaultPalette[index].red, defaultPalette[index].green,
					defaultPalette[index].blue);
		}

		final int vals[] = { 0x00, 0x5f, 0x87, 0xaf, 0xd7, 0xff };
		Assert.isTrue(index == 16);
		for (var r = 0; r < 6; r++) {
			for (var g = 0; g < 6; g++) {
				for (var b = 0; b < 6; b++) {
					palette[index] = new RGB(vals[r], vals[g], vals[b]);
					index++;
				}
			}
		}

		final int greys[] = { 0x08, 0x12, 0x1c, 0x26, 0x30, 0x3a, 0x44, 0x4e, 0x58, 0x62, 0x6c, 0x76, 0x80, 0x8a, 0x94,
				0x9e, 0xa8, 0xb2, 0xbc, 0xc6, 0xd0, 0xda, 0xe4, 0xee };

		Assert.isTrue(index == 232);
		for (final int g : greys) {
			palette[index] = new RGB(g, g, g);
			index++;
		}
		Assert.isTrue(index == 256);

	}

	/**
	 * Get a color in the range 0;255, no check performed
	 *
	 * @param index the color index
	 * @return the color
	 */
	public static RGB getColor(int index) {
		return palette[index];
	}

	/**
	 * Get a color in the range 0;255, if the index is out of range this function
	 * returns null
	 *
	 * @param index the color index
	 * @return the color or null
	 */
	public static RGB getSafeColor(int index) {
		if (index < 0 || index > 255) {
			return null;
		}
		return palette[index];
	}

	public static RGB getRgbColor(int red, int green, int blue) {
		if (red > 255 || red < 0 || green > 255 || green < 0 || blue > 255 || blue < 0) {
			return null;
		}
		return new RGB(red, green, blue);
	}

	public static String getBestPaletteForOS() {

		final var os = Platform.getOS();
		if (os == null) {
			return PALETTE_VGA;
		}

		switch (os) {
		case Platform.OS_LINUX:
			return PALETTE_XTERM;
		case Platform.OS_MACOSX:
			return PALETTE_MAC;
		case Platform.OS_WIN32:
			return PALETTE_WINXP;
		default:
			return PALETTE_VGA;
		}

	}
}
