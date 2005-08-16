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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.DebugUIMessages;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncProcessAdapter extends AbstractAsyncPresentationAdapter {

	protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
		result.done();
		return Status.OK_STATUS;
	}

	protected IStatus doRetrieveLabels(Object object, IPresentationContext context, ILabelUpdate result) {
		if (object instanceof IProcess) {
			try {
				IProcess process = (IProcess) object;
				StringBuffer label = new StringBuffer();
				if (process.isTerminated()) {
					int exit = process.getExitValue();
					label.append(MessageFormat.format(DebugUIMessages.DefaultLabelProvider_16, new String[] { new Integer(exit).toString() }));
				}
				label.append(process.getLabel());
				result.setLabel(label.toString()); //$NON-NLS-1$
				result.done();
			} catch (DebugException e) {
			}
		} else {
			// shouldn't happen
			super.doRetrieveLabels(object, context, result);
		}
		return Status.OK_STATUS;
	}

}
