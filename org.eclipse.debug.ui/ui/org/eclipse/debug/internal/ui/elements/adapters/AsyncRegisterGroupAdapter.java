package org.eclipse.debug.internal.ui.elements.adapters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncRegisterGroupAdapter extends AbstractAsyncPresentationAdapter {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#getChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, IPresentationContext context) throws CoreException {
		return ((IRegisterGroup)parent).getRegisters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.AbstractAsyncPresentationAdapter#hasChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext)
	 */
	protected boolean hasChildren(Object element, IPresentationContext context) throws CoreException {
		return ((IRegisterGroup)element).hasRegisters();
	}
	
	

}
