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
 * Adapter used to provide labels and content for elements in a tree viewer.
 * 
 * @since 3.2
 */
public interface IPresentationAdapter {

    /**
     * Retrieves the children of the given parent reporting back to the
     * given result in a non-blocking fashion.
     * 
     * @param parent the element to retrieve children for
     * @param context the context in which children have been requested
     * @param result object to report the result to
     */
    public void retrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result);
    
    /**
     * Retrieves the label of the given object reporting back to
     * the result in a non-blocking fashion.
     *  
     * @param object the element for which a label is requested
     * @param context the context in which the label has been requested
     * @param result object to report the result to
     */
    public void retrieveLabel(Object object, IPresentationContext context, ILabelUpdate result);
    

}
