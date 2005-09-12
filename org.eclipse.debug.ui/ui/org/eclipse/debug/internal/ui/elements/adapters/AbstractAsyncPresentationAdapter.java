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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;

public abstract class AbstractAsyncPresentationAdapter implements IPresentationAdapter {
	
    public void retrieveChildren(final Object parent, final IPresentationContext context, final IChildrenUpdate result) {
		Job job = new Job("Retrieving Children") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				return doRetrieveChildren(parent, context, result);
			}
		};
		job.setSystem(true);
		job.schedule();
	}

    protected abstract IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result);
        
    public void retrieveLabel(final Object object, final IPresentationContext context, final ILabelUpdate result) {
        Job job = new Job("Retrieving labels") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor monitor) {
                return doRetrieveLabel(object, context, result);
            }
        };
        job.setSystem(true);
        job.schedule();
    }
    
    protected IStatus doRetrieveLabel (Object object, IPresentationContext context, ILabelUpdate result) {
    	result.setLabel(DebugElementHelper.getLabel(object));
        result.setImageDescriptor(DebugElementHelper.getImageDescriptor(object));
        result.setFontData(DebugElementHelper.getFont(object));
        result.setBackground(DebugElementHelper.getBackground(object));
        result.setForeground(DebugElementHelper.getForeground(object));
        
        result.done();
        return Status.OK_STATUS;        
    }

}
