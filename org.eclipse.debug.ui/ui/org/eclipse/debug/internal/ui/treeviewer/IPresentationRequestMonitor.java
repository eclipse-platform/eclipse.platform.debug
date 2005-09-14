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
 * Common interface for requests made on an <code>IPresentationAdapter</code>. Results
 * of a presentation request are reported to a presentation requiest monitor (usually a
 * specialization of this interface). An presentation request may be cancelled by the client
 * making the request, or by the presentation adapter fulfilling the request. Presentation
 * adapters may report failure by setting an appropriate status on this monitor. When a request
 * is complete, an adapter must call <code>done()</code> on the monitor, no matter
 * if the update succeeded or failed. The <code>done()</code> method does not need to be
 * called if an presentation request is canceled.
 * <p>
 * Operations accepting a presentation request monitor are expected to poll the
 * monitor (using <code>isCanceled</code>) periodically and abort at their
 * earliest convenience.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.2
 */
public interface IPresentationRequestMonitor extends IProgressMonitor {

    /**
     * Sets the status of this presentation request, possibly <code>null</code>.
     * When a request fails, the status indicates why the request failed.
     * A <code>null</code> status is considered to be successful.
     * 
     * @param status request status
     */
    public void setStatus(IStatus status);
}
