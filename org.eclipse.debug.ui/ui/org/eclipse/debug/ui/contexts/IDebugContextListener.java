/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.ui.contexts;

import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * @since 3.2
 */
public interface IDebugContextListener {
	
	/**
	 * Notification the given context is the active context in the given part.
	 * When part is <code>null</code>, the context is active in the global
	 * context queue.
	 * 
	 * @param context
	 * @param part workbench part or <code>null</code>
	 */
	public void contextActivated(Object context, IWorkbenchPart part);

}
