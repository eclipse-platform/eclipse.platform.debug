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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

public class DeferredWorkbenchPresentationAdapter extends AbstractAsyncPresentationAdapter {

	private IDeferredWorkbenchAdapter fAdapter;

	public DeferredWorkbenchPresentationAdapter(IDeferredWorkbenchAdapter deferredWorkbenchAdapter) {
		fAdapter = deferredWorkbenchAdapter;
	}
	
	public void retrieveChildren(final Object parent, final IPresentationContext context, final IChildrenUpdate result) {
		Job job = new Job("Retrieving Children") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				return doRetrieveChildren(parent, context, result);
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
		fAdapter.fetchDeferredChildren(parent, new ChildElementCollector(result), result);
		return Status.OK_STATUS;
	}

	private class ChildElementCollector implements IElementCollector {
		private IChildrenUpdate result;

		ChildElementCollector(IChildrenUpdate result) {
			this.result = result;
		}
		public void add(Object element, IProgressMonitor monitor) {
			result.addChild(element);
		}

		public void add(Object[] elements, IProgressMonitor monitor) {
			result.addChildren(elements);
		}

		public void done() {
			result.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#getChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
		// NOT USED
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#hasChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
		return fAdapter.isContainer();
	}

}
