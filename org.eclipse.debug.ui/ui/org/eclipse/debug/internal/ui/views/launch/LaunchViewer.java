package org.eclipse.debug.internal.ui.views.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter;
import org.eclipse.debug.internal.ui.treeviewer.AsynchronousTreeViewer;
import org.eclipse.debug.internal.ui.treeviewer.IAsynchronousLabelAdapter;
import org.eclipse.debug.internal.ui.treeviewer.IAsynchronousTreeContentAdapter;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.osgi.framework.Bundle;

public class LaunchViewer extends AsynchronousTreeViewer {

	public LaunchViewer(Composite parent) {
		super(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.treeviewer.AsynchronousViewer#getLabelAdapter(java.lang.Object)
	 */
	protected IAsynchronousLabelAdapter getLabelAdapter(Object element) {
		AbstractAsyncPresentationAdapter legacyAdapter = getLegacyAdapter(element);
		if (legacyAdapter != null) {
			return legacyAdapter;
		}

		IAsynchronousLabelAdapter presentationAdapter = super.getLabelAdapter(element);
		if (presentationAdapter != null) {
			return presentationAdapter;
		}
		
		return new BogusAdapter();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.treeviewer.AsynchronousTreeViewer#getTreeContentAdapter(java.lang.Object)
	 */
	protected IAsynchronousTreeContentAdapter getTreeContentAdapter(Object element) {
		AbstractAsyncPresentationAdapter legacyAdapter = getLegacyAdapter(element);
		if (legacyAdapter != null) {
			return legacyAdapter;
		}

		IAsynchronousTreeContentAdapter presentationAdapter = super.getTreeContentAdapter(element);
		if (presentationAdapter != null) {
			return presentationAdapter;
		}
		
		return new BogusAdapter();
	}

	/**
	 * Returns a wrapper to the legacy workbench adapter if supported by the given object.
	 * 
	 * @param element
	 * @return
	 */
	private AbstractAsyncPresentationAdapter getLegacyAdapter(Object element) {
		if (element instanceof IDeferredWorkbenchAdapter) {
			return new WrappedDeferredWorkbenchAdapter((IDeferredWorkbenchAdapter) element, element);
		}

		if (!(element instanceof IAdaptable)) {
			return null;
		}

		IAdaptable adaptable = (IAdaptable) element;
		IDeferredWorkbenchAdapter deferred = (IDeferredWorkbenchAdapter) adaptable.getAdapter(IDeferredWorkbenchAdapter.class);
		if (deferred != null) {
			DebugUIPlugin plugin = DebugUIPlugin.getDefault();
			Bundle bundle = plugin.getBundle(deferred.getClass());
			Bundle debugBundle = plugin.getBundle();
			if (!debugBundle.equals(bundle)) {
				// if client contributed, use it
				return new WrappedDeferredWorkbenchAdapter(deferred, element);
			}

			// if the client provided an IWorkbenchAdapter, use it
			IWorkbenchAdapter nonDeferred = (IWorkbenchAdapter) adaptable.getAdapter(IWorkbenchAdapter.class);
			if (nonDeferred != null) {
				bundle = plugin.getBundle(nonDeferred.getClass());
				if (!debugBundle.equals(bundle)) {
					return new WrappedWorkbenchAdapter(nonDeferred);
				}
			}
		}
		return null;
	}

	private class ElementCollector implements IElementCollector {
		List children = new ArrayList();

		public void add(Object element, IProgressMonitor monitor) {
			children.add(element);
		}

		public void add(Object[] elements, IProgressMonitor monitor) {
			for (int i = 0; i < elements.length; i++) {
				children.add(elements[i]);
			}
		}

		public void done() {
		}

		public Object[] getChildren() {
			return children.toArray();
		}

	}

	private class WrappedDeferredWorkbenchAdapter extends AbstractAsyncPresentationAdapter {
		private IDeferredWorkbenchAdapter fAdapter;

		private Object fElement;

		public WrappedDeferredWorkbenchAdapter(IDeferredWorkbenchAdapter adapter, Object element) {
			fAdapter = adapter;
			fElement = element;
		}

		protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
			ElementCollector elementCollector = new ElementCollector();
			fAdapter.fetchDeferredChildren(fElement, elementCollector, new NullProgressMonitor());
			return elementCollector.getChildren();
		}

		protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
			if (element instanceof IStackFrame) {
				return false;
			}
			return fAdapter.isContainer();
		}
	}

	private class WrappedWorkbenchAdapter extends AbstractAsyncPresentationAdapter {
		private IWorkbenchAdapter fAdapter;

		public WrappedWorkbenchAdapter(IWorkbenchAdapter adapter) {
			fAdapter = adapter;
		}

		protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
			return fAdapter.getChildren(parent);
		}

		protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
			if (element instanceof IStackFrame) {
				return false;
			}
			return fAdapter.getChildren(element).length > 0;
		}

	}
	
	private class BogusAdapter extends AbstractAsyncPresentationAdapter {
		protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
			return new Object[0];
		}
		protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
			return false;
		}
	}

}
