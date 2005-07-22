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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;

public class AsyncStackFrameAdapter extends AbstractAsyncPresentationAdapter {

    protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
        String id = context.getPart().getSite().getId();
        IStackFrame frame = (IStackFrame) parent;
        try {
            if (id.equals(IDebugUIConstants.ID_VARIABLE_VIEW)) {
                IVariable[] variables = frame.getVariables();
                for (int i = 0; i < variables.length; i++) {
                    IVariable variable = variables[i];
                    result.addChild(variable, variable.getValue().hasVariables());
                }
            } else if (id.equals(IDebugUIConstants.ID_REGISTER_VIEW)) {
                IRegisterGroup[] registerGroups = frame.getRegisterGroups();
                for (int i = 0; i < registerGroups.length; i++) {
                    IRegisterGroup group = registerGroups[i];
                    result.addChild(group, group.hasRegisters());
                }
            }
        } catch (DebugException e) {
            IStatus status = e.getStatus();
            result.setStatus(status);
            return status;
        } finally {
            result.done();
        }
        return Status.OK_STATUS;
    }
    
    

}
