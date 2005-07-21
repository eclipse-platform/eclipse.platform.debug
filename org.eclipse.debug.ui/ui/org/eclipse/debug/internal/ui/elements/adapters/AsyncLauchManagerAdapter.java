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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.jface.resource.ImageDescriptor;

public class AsyncLauchManagerAdapter implements IPresentationAdapter {

    public void retrieveChildren(final Object parent, IPresentationContext context, final IChildrenUpdate result) {
        Job job = new Job("Retrieving children") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor monitor) {
                ILaunch[] launches = ((ILaunchManager) parent).getLaunches();
                for (int i = 0; i < launches.length; i++) {
                    ILaunch launch = launches[i];
                    result.addChild(launch, true);
                }
                result.done();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();

    }

    public void retrieveLabel(final Object object, IPresentationContext context, final ILabelUpdate result) {
        Job job = new Job("Retrieving labels") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor monitor) {
                String label = DebugElementHelper.getLabel(object);
                result.setLabel(label);
                result.done();

                ImageDescriptor imageDescriptor = DebugElementHelper.getImageDescriptor(object);
                result.setImageDescriptor(imageDescriptor);
                result.done();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
    }

}
