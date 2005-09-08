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
 * Presentation context.
 * <p>
 * Clients may instantiate this class. This class is not intended for subclassing.
 * </p>
 * @since 3.2
 */
public class PresentationContext implements IPresentationContext {
    
    private IWorkbenchPart fPart;
    private IPreferenceStore fStore;
    
    /**
     * Constructs a presentation context for the given part and preference store.
     * 
     * @param part workbench part
     * @param store preference store or <code>null</code>
     */
    public PresentationContext(IWorkbenchPart part, IPreferenceStore store) {
        fPart = part;
        fStore = store;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IPresentationContext#getPart()
     */
    public IWorkbenchPart getPart() {
        return fPart;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IPresentationContext#getPreferenceStore()
     */
    public IPreferenceStore getPreferenceStore() {
        return fStore;
    }

}
