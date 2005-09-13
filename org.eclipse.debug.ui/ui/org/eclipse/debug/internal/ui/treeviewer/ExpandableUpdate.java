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

import org.eclipse.swt.widgets.Widget;

public class ExpandableUpdate extends AbstractUpdate implements IExpandableUpdate {
	
	/**
	 * Whether the item has children
	 */
	private boolean fHasChildren = false;

	/**
	 * Constructs an update request for the given item in the given viewer.
	 * 
	 * @param item item to update
	 * @param viewer viewer the update was issued for
	 */
	public ExpandableUpdate(Widget item, AsyncTreeViewer viewer) {
		super(item, viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#performUpdate()
	 */
	protected void performUpdate() {
		getViewer().setHasChildren(getWidget(), fHasChildren);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#contains(org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate)
	 */
	protected boolean contains(AbstractUpdate update) {
		return (update instanceof ChildrenUpdate || update instanceof ExpandableUpdate) && contains(update.getWidget());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.treeviewer.IExpandableUpdate#hasChildren(java.lang.Object, boolean)
	 */
	public void hasChildren(boolean hasChildren) {
		fHasChildren = hasChildren;
	}

}
