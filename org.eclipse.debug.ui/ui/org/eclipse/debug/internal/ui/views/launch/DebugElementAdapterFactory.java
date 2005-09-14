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
import org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

/**
 * DebugElementAdapterFactory
 */
public class DebugElementAdapterFactory implements IAdapterFactory {
    
    
    private static IPresentationAdapter fgAsyncManager = new AsyncLauchManagerAdapter();
    private static IPresentationAdapter fgAsyncLaunch = new AsyncLaunchAdapter();
    private static IPresentationAdapter fgAsyncTarget = new AsyncTargetAdapter();
    private static IPresentationAdapter fgAsyncProcess = new AsyncProcessAdapter();
    private static IPresentationAdapter fgAsyncThread = new AsyncThreadAdapter();
    private static IPresentationAdapter fgAsyncFrame = new AsyncStackFrameAdapter();
    private static IPresentationAdapter fgAsyncVariable = new AsyncVariableAdapter();
    private static IPresentationAdapter fgAsyncRegisterGroup = new AsyncRegisterGroupAdapter();
    private static IPresentationAdapter fgAsyncExpressionManager = new AsyncExpressionManagerAdapter();
    private static IPresentationAdapter fgAsyncExpression = new AsyncExpressionAdapter();

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.isInstance(adaptableObject)) {
			return adaptableObject;
		}
        
        if (adapterType.equals(IPresentationAdapter.class)) {
            if (adaptableObject instanceof ILaunchManager) {
                return fgAsyncManager;
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
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] {IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IDeferredWorkbenchAdapter.class, IPresentationAdapter.class};
    }

}
