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
 * @since 3.2
 */
public interface ISourceLookupContextListener {
	
	/**
	 * Notification the given context no longer exists. The
	 * context may have resumed, terminated, or moved to a
	 * different location.
	 *  
	 * @param context
	 */
	public void contextInvalid(ISourceLookupContext context);

}
