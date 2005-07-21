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

public interface IPresentationAdapter {

    /**
     * Retrieves the children of the given parent reporting back to the
     * given result.
     * 
     * @param parent
     * @param context
     * @param result
     */
    public void retrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result);
    
    /**
     * Retrieves the label of the given object reporting back to
     * the result.
     *  
     * @param object
     * @param context
     * @param result
     */
    public void retrieveLabel(Object object, IPresentationContext context, ILabelUpdate result);
    

}
