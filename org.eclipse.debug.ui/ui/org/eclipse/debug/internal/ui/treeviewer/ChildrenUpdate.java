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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;


public class ChildrenUpdate extends AbstractUpdate implements IChildrenUpdate {
    
    private List fChildren = new ArrayList();
    private List fHasChildren = new ArrayList();

    /**
     * Constucts an update to retrieve and update the children of the given
     * item.
     * 
     * @param item
     */
    public ChildrenUpdate(Widget item, AsyncTreeViewer viewer) {
        super(item, viewer);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate#addChild(java.lang.Object, boolean)
     */
    public void addChild(Object child, boolean hasChildren) {
        fChildren.add(child);
        fHasChildren.add(new Boolean(hasChildren));
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate#addChildren(java.lang.Object[], boolean[])
     */
    public void addChildren(Object[] children, boolean[] hasChildren) {
        for (int i = 0; i < children.length; i++) {
            fChildren.add(children[i]);
            fHasChildren.add(new Boolean(hasChildren[i]));
        }
    }
    
    protected boolean contains(AbstractUpdate update) {
        return isChild(update.getItem()) || update.getItem() == getItem();
    }

    protected void performUpdate() {
        getViewer().setChildren(getItem(), fChildren, fHasChildren);
    }

}
