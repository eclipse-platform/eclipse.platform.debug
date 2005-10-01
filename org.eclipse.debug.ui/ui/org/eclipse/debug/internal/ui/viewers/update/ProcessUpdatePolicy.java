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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.viewers.AsynchronousViewer;


/**
 * Update policy for a process.
 * 
 * @since 3.2
 */
public class ProcessUpdatePolicy extends EventHandlerUpdatePolicy {
	
	private IProcess fProcess = null;
	
	/**
	 * Constructs an update policy on the given process.
	 * 
	 * @param process
	 */
	public ProcessUpdatePolicy(IProcess process) {
		fProcess = process;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.EventHandlerUpdatePolicy#init(org.eclipse.debug.ui.viewers.AsynchronousViewer)
	 */
	public void init(AsynchronousViewer viewer) {
		super.init(viewer);
		addEventHanlder(new ProcessEventHandler(viewer));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.EventHandlerUpdatePolicy#dispose()
	 */
	public void dispose() {
		super.dispose();
		fProcess = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.EventHandlerUpdatePolicy#containsEvent(org.eclipse.debug.core.DebugEvent)
	 */
	protected synchronized boolean containsEvent(DebugEvent event) {
		return fProcess.equals(event.getSource());
	}

}
