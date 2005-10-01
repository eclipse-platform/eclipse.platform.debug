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
import org.eclipse.debug.ui.viewers.AsynchronousViewer;

/**
 * Handles debug events for an event update policy in a viewer.
 * 
 * @since 3.2
 */
public abstract class DebugEventHandler {
	
	private AsynchronousViewer fViewer;
	
	/**
	 * Constructs an event handler for the given update policy.
	 * 
	 * @param policy
	 */
	public DebugEventHandler(AsynchronousViewer viewer) {
		fViewer = viewer;
	}
	
	/**
	 * Disposes this event handler
	 */
	public void dispose() {
		fViewer = null;
	}
		
	/**
	 * Returns the viewer this event handler is updating.
	 * 
	 * @return
	 */
	protected AsynchronousViewer getViewer() {
		return fViewer;
	}

	/**
	 * Handles a create event. Default implementation is to refresh the
	 * element's parent. Subclasses may override.
	 * 
	 * @param event
	 */
	public void handleCreate(DebugEvent event) {
		Object parent = getParent(event.getSource());
		if (parent != null) {
			getViewer().refresh(parent);
		}
	}
	
	/**
	 * Returns the parent of the given element or <code>null</code> if unknown.
	 * 
	 * @param element
	 * @return element's parent
	 */
	protected Object getParent(Object element) {
		return null;
	}
	
	/**
	 * Handles a terminate event. Subclasses may override.
	 * 
	 * @param event
	 */
	protected void handleTerminate(DebugEvent event) {
		getViewer().refresh(event.getSource());
	}	
	
	/**
	 * Handles a suspend event. Subclasses may override.
	 * 
	 * @param event
	 */	
	protected void handleSuspend(DebugEvent event) {
		getViewer().refresh(event.getSource());
	}
	
	/**
	 * Handles a resume event for which a suspend is expected shortly (<500ms).
	 * 
	 * @param event
	 */
	protected void handleResumeExpectingSuspend(DebugEvent event) {
		
	}
	
	/**
	 * Handles a resume event that is not expecting an immediate suspend event
	 * 
	 * @param event
	 */
	protected void handleResume(DebugEvent event) {
		getViewer().refresh(event.getSource());
	}
	
	/**
	 * Handles a change event. Subclasses may override.
	 * 
	 * @param event
	 */
	protected void handleChange(DebugEvent event) {
		if (event.getDetail() == DebugEvent.CONTENT) {
			getViewer().refresh(event.getSource());
		} else {
			getViewer().update(event.getSource());
		}
	}	

	/**
	 * Handles an unknown event. Subclasses may override.
	 * 
	 * @param event
	 */
	protected void handleOther(DebugEvent event) {
	}
	
	/**
	 * Returns whether this event handler handles the given event
	 * 
	 * @param event event to handle
	 * @return whether this event handler handles the given event
	 */
	protected abstract boolean handlesEvent(DebugEvent event);
	
	/**
	 * Notification that a pending suspend event was not received for the given
	 * resume event within the timeout period.
	 * 
	 * @param resume resume event with missing suspend event
	 */
	protected void handleSuspendTimeout(DebugEvent resume) {
		getViewer().refresh(resume.getSource());
	}
	
	/**
	 * Handles the given suspend event which caused a timeout. It is
	 * parired with its original resume event.
	 * 
	 * @param suspend suspend event
	 * @param resume resume event
	 */
	protected void handleLateSuspend(DebugEvent suspend, DebugEvent resume) {
		getViewer().refresh(suspend.getSource());
	}
}
