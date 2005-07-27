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
 * Request to retrieve the children for a presentation adapter.
 * 
 * @since 3.2
 */
public interface IChildrenUpdate extends IPresentationUpdate {

	/**
	 * Adds the given child to this update request, noting whether or not the 
	 * given child may have children.
	 * 
	 * @param child 
	 * @param hasChildren
	 */
    public void addChild(Object child, boolean hasChildren);
    
    /**
     * Adds the given children to this update request, noting whether or not the
     * children may have children.
     * 
     * @param children
     * @param hasChildren
     */
    public void addChildren(Object[] children, boolean[] hasChildren);
}
