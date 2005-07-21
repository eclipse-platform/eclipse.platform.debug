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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncLauchManagerAdapter extends AbstractAsynchPresentationAdapter {

    protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
        ILaunch[] launches = ((ILaunchManager) parent).getLaunches();
        for (int i = 0; i < launches.length; i++) {
            ILaunch launch = launches[i];
            result.addChild(launch, true);
        }
        result.done();
        return Status.OK_STATUS;
    }
}
