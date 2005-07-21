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
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;

public class AsyncTargetAdapter extends AbstractAsyncPresentationAdapter {

    protected IStatus doRetrieveChildren(Object parent, IPresentationContext context, IChildrenUpdate result) {
        try {
            IThread[] threads = ((IDebugTarget) parent).getThreads();
            for (int i = 0; i < threads.length; i++) {
                IThread thread = threads[i];
                result.addChild(thread, thread.isSuspended());
            }
        } catch (DebugException e) {
            IStatus status = e.getStatus();
            result.setStatus(status);
            return status;
        } finally {
            result.done();
        }
        return Status.OK_STATUS;
    }

}
