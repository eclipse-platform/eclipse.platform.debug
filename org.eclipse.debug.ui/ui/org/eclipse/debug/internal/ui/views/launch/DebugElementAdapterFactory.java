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
import org.eclipse.debug.internal.ui.elements.adapters.AsynchronousDebugLabelAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.DebugTargetTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.ExpressionManagerTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.ExpressionTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.LauchManagerTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.LaunchTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.ProcessTreeAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.RegisterGroupTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.StackFrameSourceLookupAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.StackFrameTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.ThreadTreeContentAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.VariableLabelAdapter;
import org.eclipse.debug.internal.ui.elements.adapters.VariableTreeContentAdapter;
import org.eclipse.debug.internal.ui.viewers.update.DefaultUpdatePolicyFactory;
import org.eclipse.debug.ui.contexts.ISourceLookupContext;
import org.eclipse.debug.ui.viewers.IAsynchronousLabelAdapter;
import org.eclipse.debug.ui.viewers.IAsynchronousTreeContentAdapter;
import org.eclipse.debug.ui.viewers.IUpdatePolicyFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

/**
 * DebugElementAdapterFactory
 */
public class DebugElementAdapterFactory implements IAdapterFactory {
	
	private static IUpdatePolicyFactory fgUpdatePolicyAdapter = new DefaultUpdatePolicyFactory();
    
    private static IAsynchronousLabelAdapter fgDebugLabelAdapter = new AsynchronousDebugLabelAdapter();
    private static IAsynchronousLabelAdapter fgVariableLabelAdapter = new VariableLabelAdapter();
    
    private static IAsynchronousTreeContentAdapter fgAsyncLaunchManager = new LauchManagerTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncLaunch = new LaunchTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncTarget = new DebugTargetTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncProcess = new ProcessTreeAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncThread = new ThreadTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncFrame = new StackFrameTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncVariable = new VariableTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncRegisterGroup = new RegisterGroupTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncExpressionManager = new ExpressionManagerTreeContentAdapter();
    private static IAsynchronousTreeContentAdapter fgAsyncExpression = new ExpressionTreeContentAdapter();

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
        
        if (adapterType.equals(IUpdatePolicyFactory.class)) {
        	if (adaptableObject instanceof ILaunch || adaptableObject instanceof IDebugTarget ||
        			adaptableObject instanceof IProcess || adaptableObject instanceof ILaunchManager ||
        			adaptableObject instanceof IStackFrame)
        	return fgUpdatePolicyAdapter;
        }
        
        if (adapterType.equals(ISourceLookupContext.class)) {
        	if (adaptableObject instanceof IStackFrame) {
        		return new StackFrameSourceLookupAdapter((IStackFrame)adaptableObject);
        	}
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] {IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IDeferredWorkbenchAdapter.class, IAsynchronousLabelAdapter.class, IAsynchronousTreeContentAdapter.class,
        		IUpdatePolicyFactory.class, ISourceLookupContext.class};
    }

}
