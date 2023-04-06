/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Simeon Andreev - pulled as base class to support enable all and disable all actions
 *******************************************************************************/
package org.eclipse.debug.internal.ui.actions.breakpoints;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.actions.AbstractRemoveAllActionDelegate;
import org.eclipse.debug.internal.ui.actions.ActionMessages;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;

abstract class AllBreakpointsEnablementAction extends AbstractRemoveAllActionDelegate implements IBreakpointsListener {

	private final boolean fEnable;
	private final String fLabel1;
	private final String fLabel2;

	AllBreakpointsEnablementAction(boolean enable, String label1, String label2) {
		this.fEnable = enable;
		this.fLabel1 = label1;
		this.fLabel2 = label2;
	}

	@Override
	protected boolean isEnabled() {
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints().length > 0;
	}

	@Override
	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		update();
	}

	@Override
	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		update();
	}

	@Override
	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		if (getAction() != null) {
			update();
		}
	}

	@Override
	protected void initialize() {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}

	@Override
	public void dispose() {
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		super.dispose();
	}


	@Override
	public void run(IAction action) {
		final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		final IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		if (breakpoints.length < 1) {
			return;
		}
		IWorkbenchWindow window = DebugUIPlugin.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
		boolean prompt = store.getBoolean(IDebugPreferenceConstants.PREF_PROMPT_DISABLE_ALL_BREAKPOINTS);
		boolean proceed = true;
		if (prompt) {
			MessageDialogWithToggle mdwt = MessageDialogWithToggle.openYesNoQuestion(window.getShell(),
					this.fLabel1, this.fLabel2,
					ActionMessages.AllBreakPointsActionEnablement_DontAskAgain, !prompt, null, null);
			if (mdwt.getReturnCode() != IDialogConstants.YES_ID) {
				proceed = false;
			} else {
				store.setValue(IDebugPreferenceConstants.PREF_PROMPT_DISABLE_ALL_BREAKPOINTS,
						!mdwt.getToggleState());
			}
		}
		if (proceed) {
			new Job(this.fLabel2) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						for (IBreakpoint breakpoint : breakpoints) {
							breakpoint.setEnabled(fEnable);
						}
					} catch (CoreException e) {
						DebugUIPlugin.log(e);
						return Status.CANCEL_STATUS;
					}
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}
}
