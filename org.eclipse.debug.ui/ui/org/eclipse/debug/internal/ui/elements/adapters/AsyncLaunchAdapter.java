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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.DebugUIMessages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncLaunchAdapter extends AbstractAsyncPresentationAdapter {

	public IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
		Object[] children = ((ILaunch) parent).getChildren();
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			result.addChild(child, !(child instanceof IProcess));
		}
		result.done();
		return Status.OK_STATUS;
	}

	protected IStatus doRetrieveLabel(Object object, IPresentationContext context, ILabelUpdate result) {
		if (object instanceof ILaunch) {
			ILaunch launch = (ILaunch) object;
			StringBuffer label = new StringBuffer(getLaunchText(launch));
			if (launch.isTerminated()) {
				label.insert(0, DebugUIMessages.DefaultLabelProvider_1); //$NON-NLS-1$
			}
			result.setLabel(label.toString());
			result.done();
			return Status.OK_STATUS;
		}

		return super.doRetrieveLabel(object, context, result);
	}

	protected String getLaunchText(ILaunch launch) {
		if (launch.getLaunchConfiguration() == null || (!launch.getLaunchConfiguration().exists() && !launch.getLaunchConfiguration().isWorkingCopy())) {
			return DebugUIMessages.DefaultLabelProvider__unknown__1; //$NON-NLS-1$
		}
		// new launch configuration
		ILaunchConfiguration config = launch.getLaunchConfiguration();
		StringBuffer buff = new StringBuffer(config.getName());
		buff.append(" ["); //$NON-NLS-1$
		try {
			buff.append(config.getType().getName());
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
		}
		buff.append("]"); //$NON-NLS-1$
		return buff.toString();
	}
}
