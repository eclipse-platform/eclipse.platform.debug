/*******************************************************************************
 * Copyright (c) 2000, 2020 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.console;

/**
 * Constants relating to the ANSI console plug-in.
 *
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IAnsiConsoleConstants {
	String P_INTERPRET_ANSI_ESCAPE_SEQUENCES = ConsolePlugin.getUniqueIdentifier()
			+ ".P_INTERPRET_ANSI_ESCAPE_SEQUENCES";//$NON-NLS-1$
	String P_SHOW_ESCAPE_SEQUENCES = ConsolePlugin.getUniqueIdentifier() + ".P_SHOW_ESCAPE_SEQUENCES";//$NON-NLS-1$
	String P_COLOR_PALETTE_NAME = ConsolePlugin.getUniqueIdentifier() + ".P_COLOR_PALETTE_NAME";//$NON-NLS-1$

	String[] P_CUSTOM_COLORS = { ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BLACK", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_RED", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_GREEN", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BROWN_YELLOW", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BLUE", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_MAGENTA", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_CYAN", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_GRAY", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_DARK_GRAY", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_GRIGHT_RED", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BRIGHT_GREEN", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_YELLOW", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BRIGHT_BLUE", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BRIGHT_MAGENTA", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_BRIGHT_CYAN", //$NON-NLS-1$
			ConsolePlugin.getUniqueIdentifier() + ".P_CUSTOM_COLOR_WHITE" //$NON-NLS-1$
	};

}
