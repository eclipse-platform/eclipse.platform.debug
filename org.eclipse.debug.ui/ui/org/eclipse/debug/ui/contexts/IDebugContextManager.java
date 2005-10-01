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

import org.eclipse.ui.IWorkbenchWindow;

/**
 * A debug context drives debugging - source lookup and action enablement in the 
 * debug user interface. The context service provides notification
 * of changes in the active context specific to the workbench, a specific window, or a
 * specific part.
 * <p>
 * Cleints provide a context policy to notifiy the context service of interesting
 * contexts within a model. For example the debug platform provides a context policy
 * that maps debug events to suspended contexts.
 * </p>
 * <p>
 * Not intended to be implemented by clients.
 * </p> 
 * @since 3.2
 */
public interface IDebugContextManager {
	
	/**
	 * Returns the debug context service for a specific window.
	 * 
	 * @param window
	 * @return
	 */
	public IDebugContextService getDebugContextService(IWorkbenchWindow window);
	
	/**
	 * Registers the given debug context provider.
	 * 
	 * @param provider
	 */
	public void addDebugContextProvider(IDebugContextProvider provider);
	
	/**
	 * Deregisters the given debug context provider.
	 * 
	 * @param provider
	 */
	public void removeDebugContextProvider(IDebugContextProvider provider);	
}
