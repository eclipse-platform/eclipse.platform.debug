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
package org.eclipse.debug.internal.ui.viewers.update;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.viewers.IPresentationContext;
import org.eclipse.debug.ui.viewers.IUpdatePolicy;
import org.eclipse.debug.ui.viewers.IUpdatePolicyFactory;

/**
 * @since 3.2
 */
public class DefaultUpdatePolicyFactory implements IUpdatePolicyFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.IUpdatePolicyFactory#createUpdatePolicy(java.lang.Object, org.eclipse.debug.ui.viewers.IPresentationContext)
	 */
	public IUpdatePolicy createUpdatePolicy(Object element, IPresentationContext context) {
		String id = context.getPart().getSite().getId();
		if (IDebugUIConstants.ID_DEBUG_VIEW.equals(id)) {
			if (element instanceof IDebugTarget) {
				return new DefaultDebugViewUpdatePolicy((IDebugTarget)element);
			}
			if (element instanceof ILaunch) {
				return new LaunchUpdatePolicy((ILaunch)element);
			}
			if (element instanceof ILaunchManager) {
				return new LaunchManagerUpdatePolicy();
			}
			if (element instanceof IProcess) {
				return new ProcessUpdatePolicy((IProcess)element);
			}
		}
		if (IDebugUIConstants.ID_VARIABLE_VIEW.equals(id)) {
			if (element instanceof IStackFrame) {
				return new DefaultVariableViewUpdatePolicy((IStackFrame)element);
			}
		}
		return null;
	}

}
