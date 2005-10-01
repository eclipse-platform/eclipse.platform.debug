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
package org.eclipse.debug.internal.ui.elements.adapters;

import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.contexts.ISourceLookupContext;
import org.eclipse.debug.ui.contexts.ISourceLookupContextListener;

/**
 * @since 3.2
 */
public class StackFrameSourceLookupAdapter implements ISourceLookupContext {

	private IStackFrame fFrame;
	
	public StackFrameSourceLookupAdapter(IStackFrame frame) {
		fFrame = frame;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceLookupContext#isSuspended()
	 */
	public boolean isSuspended() {
		return fFrame.isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceLookupContext#getSourceLookupTarget()
	 */
	public Object getSourceLookupTarget() {
		return fFrame;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceLookupContext#getSourceLocator()
	 */
	public ISourceLocator getSourceLocator() {
		return fFrame.getLaunch().getSourceLocator();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceLookupContext#init(org.eclipse.debug.ui.contexts.ISourceLookupContextListener)
	 */
	public void init(ISourceLookupContextListener listener) {
		// TODO Auto-generated method stub
		
	}

}
