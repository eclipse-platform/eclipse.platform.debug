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
 * Implementation for <code>IChildrenPresentationMonitor</code>. Collects
 * children from a presentation adapter.  
 * <p>
 * Not intended to be subclassed or instantiated by clients. For use
 * speficially with <code>AsyncTreeViewer</code>.
 * </p>
 * @since 3.2
 */
class ChildrenRequestMonitor extends PresentationRequestMonitor implements IChildrenRequestMonitor {
    
	/**
	 * Collection of children retrieved
	 */
    private List fChildren = new ArrayList();

    /**
     * Constucts a monitor to retrieve and update the children of the given
     * widget.
     * 
     * @param widget widget to retrieve children for
     */
    public ChildrenRequestMonitor(Widget widget, AsyncTreeViewer viewer) {
        super(widget, viewer);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IChildrenRequestMonitor#addChild(java.lang.Object)
     */
    public void addChild(Object child) {
        fChildren.add(child);
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IChildrenRequestMonitor#addChildren(java.lang.Object[])
     */
    public void addChildren(Object[] children) {
        for (int i = 0; i < children.length; i++) {
            fChildren.add(children[i]);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.PresentationRequestMonitor#contains(org.eclipse.debug.internal.ui.treeviewer.PresentationRequestMonitor)
     */
    protected boolean contains(PresentationRequestMonitor update) {
        return (update instanceof ChildrenRequestMonitor || update instanceof ContainerRequestMonitor) && contains(update.getWidget());
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.PresentationRequestMonitor#performUpdate()
     */
    protected void performUpdate() {
        getViewer().setChildren(getWidget(), fChildren);
    }

}
