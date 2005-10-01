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
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.viewers.AsynchronousViewer;

/**
 * Default update for a instance of a debug model in the Debug view.
 * 
 * @since 3.2
 */
public class DefaultDebugViewUpdatePolicy extends EventHandlerUpdatePolicy {
	
	/**
	 * Root model element for this update policy
	 */
	private IDebugTarget fDebugTarget;
	
	/**
	 * Constructs an update policy on the given target.
	 * 
	 * @param target
	 */
	public DefaultDebugViewUpdatePolicy(IDebugTarget target) {
		fDebugTarget = target;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.EventHandlerUpdatePolicy#dispose()
	 */
	public void dispose() {
		super.dispose();
		fDebugTarget = null;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.IUpdatePolicy#init(org.eclipse.debug.ui.viewers.update.IPresentation)
	 */
	public void init(AsynchronousViewer viewer) {
		super.init(viewer);
		addEventHanlder(new DebugTargetEventHandler(viewer));
		addEventHanlder(new ThreadEventHandler(viewer));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.EventHandlerUpdatePolicy#containsEvent(org.eclipse.debug.core.DebugEvent)
	 */
	protected boolean containsEvent(DebugEvent event) {
		Object source = event.getSource();
		if (source instanceof IDebugElement) {
			return fDebugTarget.equals(((IDebugElement)source).getDebugTarget());
		}
		return false;
	}

}
