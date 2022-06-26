/*******************************************************************************
 * Copyright (c) 2013 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.ui.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.DebugUIMessages;

/**
 * @author Kris De Volder
 * @author V Udayani
 * @since 3.16
 */
public class StopProcessPullDownToolbarDelegate extends AbstractLaunchToolbarPulldown {

	@Override
	protected void performOperation(LaunchList.Item launch) throws DebugException {
		LaunchUtils.terminate(launch.conf);
	}

	@Override
	protected String getOperationName() {
		return DebugUIMessages.AbstractLaunchToolbarPulldown_Stop;
	}

	@Override
	protected LaunchList createList() {
		// TODO Replace with something that retains launchconfigs even after its processes are
		//   terminated.
		return LiveProcessTracker.getInstance();
	}

}
