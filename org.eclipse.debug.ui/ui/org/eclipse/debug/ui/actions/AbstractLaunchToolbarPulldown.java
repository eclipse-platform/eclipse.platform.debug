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

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.internal.ui.DebugUIMessages;
import org.eclipse.debug.ui.actions.LaunchList.Item;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * Abstract superclass for an launch toolbar button with pulldown that applies
 * some operation to launchConfiguration from some historic list of launch
 * configs.
 *
 * @author Kris De Volder
 * @author V Udayani
 * @author Karthik Sankaranarayanan
 * @since 3.16
 */
public abstract class AbstractLaunchToolbarPulldown implements IWorkbenchWindowPulldownDelegate, ILaunchConfigurationListener {

	private IWorkbenchWindow window;
	private Menu menu;
	private LaunchList.Listener launchListener;
	private LaunchList launches = createList().addListener(launchListener = new LaunchList.Listener() {
		@Override
		public void changed() {
			if (action!=null && window!=null) {
				uiUpdate();
			}
		}

	});
	private IAction action;


	public AbstractLaunchToolbarPulldown() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(this);
	}

	/**
	 * Factory method to create or obtain an instance that keeps track of the launches to be
	 * shown in the pulldown menu.
	 */
	protected abstract LaunchList createList();

	@Override
	public void run(IAction action) {
		this.action = action;
		final LaunchList.Item l = launches.getLast();
		if (l!=null) {
			Job job = new Job(getOperationName()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						performOperation(l);
						return Status.OK_STATUS;
					} catch (Exception e) {
						return Status.error(getOperationName(), e);
					}
				}
			};
			job.schedule();
		} else {
			MessageDialog.openError(window.getShell(), DebugUIMessages.AbstractLaunchToolbarPulldown_No_Processes_Found,
					DebugUIMessages.AbstractLaunchToolbarPulldown_Rendering_1 + getOperationName()
							+ DebugUIMessages.AbstractLaunchToolbarPulldown_No_Active_Processes
			);
		}
	}

	protected abstract String getOperationName();

	protected abstract void performOperation(LaunchList.Item l) throws DebugException;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
		update();
	}

	/**
	 * Update labels etc. This method must only be called from the UI thread.
	 */
	private void update() {
		if (action!=null) {
			Item launch = launches.getLast();
			String label = getOperationName();
			if (launch!=null) {
				label = label + " " + launch.getName(); //$NON-NLS-1$
			}
			action.setText(label);
			action.setToolTipText(label);
			action.setEnabled(launch!=null);
		}
	}

	/**
	 * Update labels etc. This method may be called from a non-ui thread.
	 */
	private void uiUpdate() {
		Shell shell = window.getShell();
		if (shell!=null) {
			//We may not be in the UIThread here. So take care before futzing with the widgets!
			Display display = shell.getDisplay();
			if (display!=null) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						update();
					}
				});
			}
		}
	}



	@Override
	public void dispose() {
		if (menu!=null) {
			menu.dispose();
			menu = null;
		}
		if (launches!=null && launchListener!=null) {
			launches.removeListener(launchListener);
			launches = null;
		}
		DebugPlugin.getDefault().getLaunchManager().removeLaunchConfigurationListener(this);
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		update();
	}

	@Override
	public Menu getMenu(Control parent) {
		if (menu!=null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		fillMenu();
		return menu;
	}

	private void fillMenu() {
		new SubMenuProvider(launches).fill(menu, -1);
	}

	private static final IContributionItem EMPTY_ITEM = new ActionContributionItem(
			new EmptyAction(DebugUIMessages.AbstractLaunchToolbarPulldown_No_Active_Processes));

	/**
	 * An action that is disabled and does nothing. Its only purpose is to
	 * show text when there is nothing else to show in an otherwise empty menu.
	 */
	private static class EmptyAction extends Action {
		public EmptyAction(String label) {
			super(label);
		}
		@Override
		public boolean isEnabled() {
			return false;
		}
	}


	/**
	 * Dynamically creates menus to terminate currently active launches.
	 */
	private class SubMenuProvider extends CompoundContributionItem {

		private final LaunchList launches;

		public SubMenuProvider(LaunchList launches) {
			this.launches = launches;
		}


		private class PerformAction extends Action {
			private final Item launch;

			public PerformAction(LaunchList.Item launch) {
				this.launch = launch;
				this.setText(launch.getName());
			}

			@Override
			public void run() {
				try {
					performOperation(launch);
				} catch (DebugException e) {
					DebugPlugin.log(e);

				}
			}

		}

		/**
		 * An action that is used to terminate all running processes.
		 */
		private class PerformAllAction extends Action {

			public PerformAllAction(String label) {
				super(label);
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public void run() {
				try {
					for (Item launch : launches.getLaunches()) {
						performOperation(launch);
					}
				} catch (DebugException e) {
					DebugPlugin.log(e);
				}
			}
		}

		private final IContributionItem TERMINATE_ALL = new ActionContributionItem(
				new PerformAllAction(DebugUIMessages.AbstractLaunchToolbarPulldown_Terminate_All));

		@Override
		protected IContributionItem[] getContributionItems() {
			return createContributionItems();
		}

		private IContributionItem[] createContributionItems() {
			ArrayList<IContributionItem> items = new ArrayList<>();

			if (!launches.getLaunches().isEmpty()) {
				items.add(TERMINATE_ALL);
			}
			for (Item launch : launches.getLaunches()) {
				items.add(new ActionContributionItem(new PerformAction(launch)));
			}
			if (items.isEmpty()) {
				items.add(EMPTY_ITEM);
			}
			// Return item in reverse order (so older item at the bottom of the menu).
			IContributionItem[] array = new IContributionItem[items.size()];
			for (int i = 0; i < array.length; i++) {
				array[array.length - i - 1] = items.get(i);
			}
			return array;
		}

	}

	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		uiUpdate(); // force update tooltip on button in case label changed on account of the
		// config it launches got added
	}

	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
	}

	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		uiUpdate(); // force update tooltip on button in case label changed on account of the
		            // config it launches got renamed or deleted
	}


}
