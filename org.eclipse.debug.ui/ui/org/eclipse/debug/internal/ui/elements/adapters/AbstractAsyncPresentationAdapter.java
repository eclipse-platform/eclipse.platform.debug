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
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.resource.ImageDescriptor;

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
                return doRetrieveLabels(object, context, result);
            }
        };
        job.setSystem(true);
        job.schedule();
    }
    
    protected IStatus doRetrieveLabels (Object object, IPresentationContext context, ILabelUpdate result) {
        String label = DebugElementHelper.getLabel(object);
        result.setLabel(label);
        result.done();

        ImageDescriptor imageDescriptor = DebugUITools.getDefaultImageDescriptor(object);
        result.setImageDescriptor(imageDescriptor);
        result.done();
        return Status.OK_STATUS;        
    }

}
