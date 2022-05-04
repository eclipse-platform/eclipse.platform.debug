/*******************************************************************************
 * Copyright (c) 2013 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author Kris De Volder
 * @author V Udayani
 * @author Karthik Sankaranarayanan
 * @since 3.16
 */
public class LaunchUtils {


	/**
	 * Terminate all launches in a list.
	 * This operation may be asynchronous. The caller can not rely
	 * on the launches being terminated before the method returns.
	 */
	public static void terminate(List<ILaunch> launches) throws DebugException {
		for (ILaunch l : launches) {
			if (!l.isTerminated()) {
				l.terminate();
			}
		}
	}

	public static List<ILaunch> getLaunches(ILaunchConfiguration launchConf) {
		ILaunch[] all = DebugPlugin.getDefault().getLaunchManager().getLaunches();
		ArrayList<ILaunch> selected = new ArrayList<>();
		for (ILaunch l : all) {
			ILaunchConfiguration lConf = l.getLaunchConfiguration();
			if (lConf!=null && lConf.equals(launchConf)) {
				selected.add(l);
			}
		}
		return selected;
	}

	/**
	 * Terminates all launches associated with given launch config.
	 * This operation may be asynchronous. The caller can not rely
	 * on the launches being terminated before the method returns.
	 */
	public static void terminate(ILaunchConfiguration conf) throws DebugException {
		terminate(getLaunches(conf));
	}


}
