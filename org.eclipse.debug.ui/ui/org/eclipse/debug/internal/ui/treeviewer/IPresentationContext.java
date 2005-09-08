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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context in which a label or children have been requested.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 3.2
 */
public interface IPresentationContext {
    
    /**
     * Returns the part for which a presentation request is being made
     * or <code>null</code> if none. 
     * 
     * @return the part for which a presentation request is being made
     * or <code>null</code>
     */
    public IWorkbenchPart getPart();
    
    /**
     * Returns the preference settings associated with the preference 
     * request, or <code>null</code> if none.
     * 
     * @return the preference settings associated with the preference 
     * request, or <code>null</code>
     */
    public IPreferenceStore getPreferenceStore();
}
