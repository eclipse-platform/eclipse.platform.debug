package org.eclipse.debug.internal.ui.treeviewer;

import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter;
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
			result.addChild(element, true);
		}

		public void add(Object[] elements, IProgressMonitor monitor) {
			boolean[] hasChildren = new boolean[elements.length];
			Arrays.fill(hasChildren, fAdapter.isContainer());
			result.addChildren(elements, hasChildren);
		}

		public void done() {
			result.done();
		}
	}

}
