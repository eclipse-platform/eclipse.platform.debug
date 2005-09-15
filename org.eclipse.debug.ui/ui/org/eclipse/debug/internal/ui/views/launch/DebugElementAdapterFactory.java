/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.views.launch;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncExpressionAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncExpressionManagerAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncLauchManagerAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncLaunchAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncProcessAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncRegisterGroupAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncStackFrameAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncTargetAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncThreadAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncVariableAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsyncVariableLabelAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.AsynchronousDebugLabelAdapter;
import org.eclipse.debug.ui.viewers.IAsynchronousLabelAdapter;
import org.eclipse.debug.ui.viewers.IAsynchronousTreeContentAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

/**
 * DebugElementAdapterFactory
 */
public class DebugElementAdapterFactory implements IAdapterFactory {
    
    private static IAsynchronousLabelAdapter fgDebugLabelAdapter = new AsynchronousDebugLabelAdapter();
    private static IAsynchronousLabelAdapter fgVariableLabelAdapter = new AsyncVariableLabelAdapter();
    
    private static IAsynchronousTreeContentAdapter fgAsyncLaunchManager = new AsyncLauchManagerAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncLaunch = new AsyncLaunchAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncTarget = new AsyncTargetAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncProcess = new AsyncProcessAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncThread = new AsyncThreadAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncFrame = new AsyncStackFrameAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncVariable = new AsyncVariableAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncRegisterGroup = new AsyncRegisterGroupAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncExpressionManager = new AsyncExpressionManagerAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncExpression = new AsyncExpressionAdapter();

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.isInstance(adaptableObject)) {
			return adaptableObject;
		}
        
        if (adapterType.equals(IAsynchronousTreeContentAdapter.class)) {
            if (adaptableObject instanceof ILaunchManager) {
                return fgAsyncLaunchManager;
            }
            if (adaptableObject instanceof ILaunch) {
                return fgAsyncLaunch;
            }
            if (adaptableObject instanceof IDebugTarget) {
                return fgAsyncTarget;
            }
            if (adaptableObject instanceof IProcess) {
                return fgAsyncProcess;
            }
            if (adaptableObject instanceof IThread) {
                return fgAsyncThread;
            }
            if (adaptableObject instanceof IStackFrame) {
                return fgAsyncFrame;
            }
            if (adaptableObject instanceof IVariable) {
                return fgAsyncVariable;
            }
            if (adaptableObject instanceof IRegisterGroup) {
            		return fgAsyncRegisterGroup;
            }
            if (adaptableObject instanceof IExpressionManager) {
            	return fgAsyncExpressionManager;
            }
            if (adaptableObject instanceof IExpression) {
            	return fgAsyncExpression;
            }
        }
        
        if (adapterType.equals(IAsynchronousLabelAdapter.class)) {
        	if (adaptableObject instanceof IVariable) {
        		return fgVariableLabelAdapter;
        	}
        	return fgDebugLabelAdapter;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] {IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IDeferredWorkbenchAdapter.class, IAsynchronousLabelAdapter.class, IAsynchronousTreeContentAdapter.class};
    }

}
