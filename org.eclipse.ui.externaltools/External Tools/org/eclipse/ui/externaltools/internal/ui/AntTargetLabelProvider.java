package org.eclipse.ui.externaltools.internal.ui;

/**********************************************************************
Copyright (c) 2002 IBM Corp. and others.
All rights reserved.   This program and the accompanying materials
are made available under the terms of the Common Public License v0.5
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v05.html
 
Contributors:
**********************************************************************/
import java.util.ArrayList;

import org.eclipse.ant.core.TargetInfo;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.externaltools.internal.core.ToolMessages;

/**
 * Ant target label provider
 */
public class AntTargetLabelProvider extends LabelProvider {
	private ArrayList selectedTargetNames = null;

	/* (non-Javadoc)
	 * Method declared on ILabelProvider.
	 */
	public String getText(Object model) {
		TargetInfo targetToDisplay = (TargetInfo) model;
		if (targetToDisplay != null) {
			StringBuffer result = new StringBuffer(targetToDisplay.getName());
			if (targetToDisplay.isDefault()) {
				result.append(" ("); //$NON-NLS-1$;
				result.append(ToolMessages.getString("AntTargetLabelProvider.defaultTarget")); //$NON-NLS-1$;
				result.append(")"); //$NON-NLS-1$;
			}
			if (selectedTargetNames != null) {
				int targetIndex = selectedTargetNames.indexOf(targetToDisplay.getName());
				if (targetIndex >= 0) {
					result.append(" ["); //$NON-NLS-1$;
					result.append(targetIndex + 1);
					result.append("]"); //$NON-NLS-1$;
				}
			}
			return result.toString();
		} else {
			return null;
		}
	}

	/**
	 * Sets the targets selected in the viewer.
	 */
	public void setSelectedTargetNames(ArrayList value) {
		selectedTargetNames = value;
	}
}