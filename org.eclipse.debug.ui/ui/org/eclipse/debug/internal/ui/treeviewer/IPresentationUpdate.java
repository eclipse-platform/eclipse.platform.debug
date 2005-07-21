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
package org.eclipse.debug.internal.ui.treeviewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * <p>
 * Operations taking a presentation result are expected to poll the
 * result(using <code>isCanceled</code>) periodically and abort at their
 * earliest convenience.  Operation can however choose to ignore cancelation
 * requests.
 * </p>
 * 
 * @since 3.2
 */
public interface IPresentationUpdate extends IProgressMonitor {

    /**
     * Sets the status of this request, possibly <code>null</code>.
     * When a request fails, the status indicates why. A <code>null</code>
     * status is considered to be successful.
     * 
     * @param status request status
     */
    public void setStatus(IStatus status);
}
