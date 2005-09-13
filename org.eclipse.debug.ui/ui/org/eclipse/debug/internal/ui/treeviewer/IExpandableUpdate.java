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
package org.eclipse.debug.internal.ui.treeviewer;

/**
 * Update request to determine if an element has children. A presentation adapter
 * notifies this object when it determines whether it has children.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.2
 */
public interface IExpandableUpdate extends IPresentationUpdate {

	/**
	 * Notification on whether an element has children.
	 * 
	 * @param hasChildren whether the element has children
	 */
    public void hasChildren(boolean hasChildren);
    
}
