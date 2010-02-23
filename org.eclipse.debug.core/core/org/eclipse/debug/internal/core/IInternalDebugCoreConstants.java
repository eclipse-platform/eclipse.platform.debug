/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.core;

import org.eclipse.debug.core.DebugPlugin;

/**
 * Contains constants to be used internally in all debug components
 */
public interface IInternalDebugCoreConstants {

	/**
	 * Boolean preference controlling whether status handler extensions
	 * are enabled. Default value is <code>true</code>. When disabled
	 * any call to {@link DebugPlugin#getStatusHandler(IStatus)} will return <code>null</code>.
	 */
	public static final String PREF_ENABLE_STATUS_HANDLERS = DebugPlugin.getUniqueIdentifier() + ".PREF_ENABLE_STATUS_HANDLERS"; //$NON-NLS-1$

}
