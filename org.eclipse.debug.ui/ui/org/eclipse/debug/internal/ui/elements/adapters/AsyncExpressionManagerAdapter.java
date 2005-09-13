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

package org.eclipse.debug.internal.ui.elements.adapters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncExpressionManagerAdapter extends AbstractAsyncPresentationAdapter {

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#doRetrieveLabel(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext, org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate)
     */
    protected IStatus doRetrieveLabel(Object object, IPresentationContext context, ILabelUpdate result) {
        result.done();
        return Status.OK_STATUS;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#getChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
		return ((IExpressionManager) parent).getExpressions();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#hasChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
		return ((IExpressionManager)element).hasExpressions();
	}
    
    
}
