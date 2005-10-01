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

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.ui.viewers.AsynchronousViewer;

/**
 * Update policy that responds to launch change notification.
 * 
 * @since 3.2
 */
public abstract class LaunchListenerUpdatePolicy extends AbstractUpdatePolicy implements ILaunchesListener2 {


	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.IUpdatePolicy#init(org.eclipse.debug.ui.viewers.update.IPresentation)
	 */
	public void init(AsynchronousViewer viewer) {
		super.init(viewer);
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.IUpdatePolicy#dispose()
	 */
	public void dispose() {
		super.dispose();
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.viewers.IUpdatePolicy#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			// TODO: referesh root
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
		} else {
			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		}
	}

}
