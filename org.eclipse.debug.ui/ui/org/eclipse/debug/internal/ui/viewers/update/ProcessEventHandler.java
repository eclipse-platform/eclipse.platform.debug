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
 * Event handler for a process
 */
public class ProcessEventHandler extends DebugEventHandler {

	/**
	 * Contstructs an event handler for a process in the given viewer.
	 * 
	 * @param viewer
	 */
	public ProcessEventHandler(AsynchronousViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#handlesEvent(org.eclipse.debug.core.DebugEvent)
	 */
	protected boolean handlesEvent(DebugEvent event) {
		return event.getSource() instanceof IProcess;
	}

}
