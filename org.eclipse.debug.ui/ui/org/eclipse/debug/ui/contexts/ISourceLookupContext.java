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

import org.eclipse.debug.core.model.ISourceLocator;

/**
 * Represents a specific execution location in a program.
 * Obtained as an adapter from an execution context.
 * 
 * @since 3.2
 */
public interface ISourceLookupContext {
	
	/**
	 * Whether this context is currently suspended.
	 * 
	 * @return
	 */
	public boolean isSuspended();

	/**
	 * Returns the object in this context that should be used
	 * to lookup source in this context's source locator.
	 * 
	 * @return
	 */
	public Object getSourceLookupTarget();
	
	/**
	 * Returns the source locator to use for this context.
	 * 
	 * @return
	 */
	public ISourceLocator getSourceLocator();
	
	/**
	 * Initializes this adapter on the given listener. Once this context
	 * becomes invlaid, the listener cleared so this adapter can be
	 * garbage collected.
	 * 
	 * @param listener
	 */
	public void init(ISourceLookupContextListener listener);
	
}
