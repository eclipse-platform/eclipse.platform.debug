/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Pawel Piech - Bug 75183
 *******************************************************************************/

package org.eclipse.debug.internal.ui.contexts.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.contexts.DebugContextManager;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractListenerActionDelegate extends AbstractContextActionDelegate implements IActionDelegate2, IDebugContextListener {

	private boolean fDisposed;

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public synchronized void dispose() {
		super.dispose();
		fDisposed = true;
		
		IWorkbenchWindow window = getWindow();
		IViewPart view = getView();
		if (view != null) {
		String id = view.getSite().getId();
		DebugContextManager.getDefault().removeDebugContextListener(this, window, id);
		} else {
			DebugContextManager.getDefault().removeDebugContextListener(this, window);
		}

	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		super.init(window);
		DebugContextManager.getDefault().addDebugContextListener(this, window);
	}

	/**
	 * @see IViewActionDelegate#init(IViewPart)
	 */
	public void init(IViewPart view) {
		super.init(view);
		IWorkbenchWindow window = view.getViewSite().getWorkbenchWindow();
		setWindow(window);
		DebugContextManager.getDefault().addDebugContextListener(this, window, view.getSite().getId());
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction,
	 *      org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
	
	
	protected void doAction(Object element) throws DebugException {
		// TODO Auto-generated method stub
		
	}

	
	public void contextActivated(ISelection context, IWorkbenchPart part) {
		if (getWindow() == null || getAction() == null) {
			return;
		}
		Shell shell= getWindow().getShell();
		if (shell == null || shell.isDisposed()) {
			return;
		}
        synchronized (this) {
            if (fDisposed) {
                return;
            }
        }
        
        update(getAction(), context);
	}
	
}
