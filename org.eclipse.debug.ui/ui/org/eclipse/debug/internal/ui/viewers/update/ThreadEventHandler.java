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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.viewers.AsynchronousTreeViewer;
import org.eclipse.debug.ui.viewers.AsynchronousViewer;
import org.eclipse.debug.ui.viewers.TreePath;
import org.eclipse.debug.ui.viewers.TreeSelection;
import org.eclipse.jface.viewers.ISelection;


/**
 * @since 3.2
 * 
 * TODO: how does selection fit into this? source lookup, etc.
 */
public class ThreadEventHandler extends DebugEventHandler {
	
	/**
	 * Constructs and event handler for a threads in the given viewer.
	 * 
	 * @param viewer
	 */
	public ThreadEventHandler(AsynchronousViewer viewer) {
		super(viewer);
	}

	/**
	 * Map of threads to top stack frames
	 */
	private Map fTopFrames = new HashMap();

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#dispose()
	 */
	public void dispose() {
		super.dispose();
		fTopFrames.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#getParent(java.lang.Object)
	 */
	protected Object getParent(Object element) {
		return ((IThread)element).getDebugTarget();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#handleSuspend(org.eclipse.debug.core.DebugEvent)
	 */
	protected void handleSuspend(DebugEvent event) {
		IThread thread = (IThread)event.getSource();
		IStackFrame frame = null;
		try {
			frame = thread.getTopStackFrame();
		} catch (DebugException e) {
		}
		IStackFrame prevFrame = null;
		synchronized (fTopFrames) {
			prevFrame = (IStackFrame) fTopFrames.remove(thread);
			if (frame != null) {
				// cache for the next suspend
				fTopFrames.put(thread, frame);
			}
		}
		if (prevFrame == null) {
			getViewer().refresh(thread);
		} else {
			if (frame != null && frame.equals(prevFrame)) {
				getViewer().update(frame);
			} else {
				getViewer().refresh(thread);
			}
		}
		TreePath path = getPath(frame);
		ISelection selection = new TreeSelection(new TreePath[]{path});
		((AsynchronousTreeViewer)getViewer()).expand(selection);
		getViewer().setSelection(selection, true);
	}
	
	protected TreePath getPath(IStackFrame frame) {
		return new TreePath(new Object[]{DebugPlugin.getDefault().getLaunchManager(), frame.getLaunch(), frame.getDebugTarget(), frame.getThread(), frame});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#handleResume(org.eclipse.debug.core.DebugEvent)
	 */
	protected void handleResume(DebugEvent event) {
		synchronized (fTopFrames) {
			// remove the last top frame for the thread
			fTopFrames.remove(event.getSource());
		}
		super.handleResume(event);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler#handlesEvent(org.eclipse.debug.core.DebugEvent)
	 */
	protected boolean handlesEvent(DebugEvent event) {
		return event.getSource() instanceof IThread;
	}
	
}
