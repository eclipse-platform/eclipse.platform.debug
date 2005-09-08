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
 * Provides labels and children for elements in a tree viewer. Note that implementations
 * are intended to provide labels and children asynchronously. 
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.2
 */
public interface IPresentationAdapter {

    /**
     * Asynchronously retrieves the children of the given parent reporting back to the
     * given result. If unable to retrieve children, an exception should be reported
     * to the result object with an appropriate status.
     * 
     * @param parent the element to retrieve children for
     * @param context the context in which children have been requested
     * @param result object to report children to
     */
    public void retrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result);
    
    /**
     * Asynchronously retrieves the label of the given object reporting back to
     * the given result. If unable to retrieve label information, an exception should be
     * reported back to the result object with an appropriate status.
     *  
     * @param object the element for which a label is requested
     * @param context the context in which the label has been requested
     * @param result object to report the result to
     */
    public void retrieveLabel(Object object, IPresentationContext context, ILabelUpdate result);
    

}
