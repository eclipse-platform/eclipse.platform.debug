package org.eclipse.debug.internal.ui.elements.adapters;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncRegisterGroupAdapter extends AbstractAsyncPresentationAdapter {

	protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
		try {
			IRegister[] registers = ((IRegisterGroup)parent).getRegisters();
			boolean[] hasKids = new boolean[registers.length];
			Arrays.fill(hasKids, false);
			result.addChildren(registers, hasKids);
			result.done();
			return Status.OK_STATUS;
		} catch (DebugException e) {
		}
		return Status.CANCEL_STATUS;
	}

}
