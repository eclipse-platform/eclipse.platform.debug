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

/**
 * Request to update the children of an element in a tree. 
 * <p>
 * Not intended to be subclassed or instantiated by clients. For use
 * speficially with <code>AsyncTreeViewer</code>.
 * </p>
 * @since 3.2
 */
class ChildrenUpdate extends AbstractUpdate implements IChildrenUpdate {
    
	/**
	 * Collection of children retrieved
	 */
    private List fChildren = new ArrayList();
    
    /**
     * Each entry is a boolean corresponding to whether asscoaited
     * child if <code>fChildren</code> has children.
     */
    private List fHasChildren = new ArrayList();

    /**
     * Constucts an request to retrieve and update the children of the given
     * widget.
     * 
     * @param widget widget to retrieve children for
     */
    public ChildrenUpdate(Widget widget, AsyncTreeViewer viewer) {
        super(widget, viewer);
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
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#contains(org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate)
     */
    protected boolean contains(AbstractUpdate update) {
        return (update instanceof ChildrenUpdate) && contains(update.getWidget());
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#performUpdate()
     */
    protected void performUpdate() {
        getViewer().setChildren(getWidget(), fChildren, fHasChildren);
    }

}
