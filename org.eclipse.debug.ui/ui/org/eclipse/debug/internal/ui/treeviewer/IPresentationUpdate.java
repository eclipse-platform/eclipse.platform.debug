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
 * Base interface for update requests on an <code>IPresentationAdapter</code>. Results
 * of an update request are reported back to an update object. An update request
 * may be cancelled by the client requesting the update, or the adapter performing the
 * update. Adapters may report failure by setting an appropriate status. When a request
 * is complete, an adapter must call <code>done()</code> on this update object.
 * <p>
 * Operations accepting a presentation update are expected to poll the
 * update (using <code>isCanceled</code>) periodically and abort at their
 * earliest convenience.
 * </p>
 * @since 3.2
 */
public interface IPresentationUpdate extends IProgressMonitor {

    /**
     * Sets the status of this request, possibly <code>null</code>.
     * When a request fails, the status indicates why the request failed.
     * A <code>null</code> status is considered to be successful.
     * 
     * @param status request status
     */
    public void setStatus(IStatus status);
}
