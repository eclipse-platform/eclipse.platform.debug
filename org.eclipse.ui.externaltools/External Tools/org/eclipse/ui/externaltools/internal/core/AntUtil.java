package org.eclipse.ui.externaltools.internal.core;

/**********************************************************************
Copyright (c) 2002 IBM Corp. and others.
All rights reserved.   This program and the accompanying materials
are made available under the terms of the Common Public License v0.5
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v05.html
 
Contributors:
**********************************************************************/
import org.eclipse.ant.core.*;
import org.eclipse.core.runtime.*;

/**
 * General utility class dealing with Ant files
 */
public final class AntUtil {
	private static final String ATT_DEFAULT = "default"; //NON-NLS-1$
	private static final String ATT_NAME = "name"; //NON-NLS-1$
	private static final String TAG_TARGET = "target"; //NON-NLS-1$
	
	/**
	 * No instances allowed
	 */
	private AntUtil() {
		super();
	}

	/**
	 * Returns the list of targets for the Ant file specified by the provided
	 * IPath, or <code>null</code> if no Ant targets found.
	 * 
	 * @throws CoreException if file does not exist, IO problems, or invalid format.
	 */
	public static TargetInfo[] getTargetList(String path) throws CoreException {
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(path);
	 	return runner.getAvailableTargets();
	}
}
