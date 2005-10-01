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

/**
 * Maintains the active context for a debug model. Should not
 * register with the debug context manager explicitly. The
 * context policy manager multiplexes these.
 * 
 * @since 3.2
 */
public interface IDebugContextPolicy extends IDebugContextProvider {
	
	public void init();
	
	public void dispose();

}
