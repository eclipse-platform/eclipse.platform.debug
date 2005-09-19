package org.eclipse.debug.internal.ui.elements.adapters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.ui.viewers.AsynchronousTreeContentAdapter;
import org.eclipse.debug.ui.viewers.IPresentationContext;

public class RegisterGroupTreeContentAdapter extends AsynchronousTreeContentAdapter {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.AsynchronousTreeContentAdapter#getChildren(java.lang.Object, org.eclipse.debug.ui.viewers.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
		return ((IRegisterGroup)parent).getRegisters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.AsynchronousTreeContentAdapter#hasChildren(java.lang.Object, org.eclipse.debug.ui.viewers.IPresentationContext)
	 */
	protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
		return ((IRegisterGroup)element).hasRegisters();
	}
	
}
