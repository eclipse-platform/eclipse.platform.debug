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
package org.eclipse.debug.internal.ui.sourcelookup;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.internal.ui.contexts.DebugContextManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.debug.ui.contexts.ISourceLookupContext;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

/**
 * Performs source lookup in a window.
 * 
 * @since 3.2
 */
public class SourceLookupService implements IDebugContextListener {
	
	private IWorkbenchWindow fWindow;
	
	private Object fPrevTarget;
	private ISourceLookupResult fPrevResult;
	
	public SourceLookupService(IWorkbenchWindow window) {
		fWindow = window;
		DebugContextManager.getDefault().getDebugContextService(window).addDebugContextListener(this);
	}
	
	public void dispose() {
		DebugContextManager.getDefault().getDebugContextService(fWindow).removeDebugContextListener(this);
	}
	
	/**
	 * A job to perform source lookup on the currently selected stack frame.
	 */
	class SourceLookupJob extends Job {
		
		private Object fTarget;
		private ISourceLocator fLocator;
		private IWorkbenchPage fPage;

		/**
		 * Constructs a new source lookup job.
		 */
		public SourceLookupJob(Object target, ISourceLocator locator, IWorkbenchPage page) {
			super("Debug Source Lookup"); 
			setPriority(Job.INTERACTIVE);
			setSystem(true);
			fTarget = target;
			fLocator = locator;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {
				ISourceLookupResult result = null;
				result = DebugUITools.lookupSource(fTarget, fLocator);
				synchronized (SourceLookupService.this) {
					fPrevResult = result;
					fPrevTarget = fTarget;
				}
				if (!monitor.isCanceled()) {
					SourceDisplayJob job = new SourceDisplayJob(result, fPage);
					job.schedule();
				}
			}
			return Status.OK_STATUS;
		}
		
	}
	
	class SourceDisplayJob extends UIJob {
		
		private ISourceLookupResult fResult;
		private IWorkbenchPage fPage;

		/**
		 * Constructs a new source display job
		 */
		public SourceDisplayJob(ISourceLookupResult result, IWorkbenchPage page) {
			super("Debug Source Display"); 
			setSystem(true);
			setPriority(Job.INTERACTIVE);
			fResult = result;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {
				DebugUITools.displaySource(fResult, fPage);
			}
			return Status.OK_STATUS;
		}
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextListener#contextActivated(java.lang.Object, org.eclipse.ui.IWorkbenchPart)
	 */
	public synchronized void contextActivated(Object context, IWorkbenchPart part) {
		if (context instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) context;
			ISourceLookupContext adapter = (ISourceLookupContext) adaptable.getAdapter(ISourceLookupContext.class);
			if (adapter != null) {
				IWorkbenchPage page = part.getSite().getPage();
				Object target = adapter.getSourceLookupTarget();
				if (target.equals(fPrevTarget)) {
					(new SourceDisplayJob(fPrevResult, page)).schedule();
				} else {
					(new SourceLookupJob(target, adapter.getSourceLocator(), page)).schedule();
				}
			}
		}
		// TODO: listen for changes in source lookup context to clear
		// TODO: distinguish top/secondary frame?
		
	}
}
