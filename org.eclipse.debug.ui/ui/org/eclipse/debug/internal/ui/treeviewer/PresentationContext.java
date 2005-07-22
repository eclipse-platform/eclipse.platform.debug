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

public class PresentationContext implements IPresentationContext {
    
    private IWorkbenchPart fPart;
    private IPreferenceStore fStore;
    
    public PresentationContext(IWorkbenchPart part, IPreferenceStore store) {
        fPart = part;
        fStore = store;
    }

    public IWorkbenchPart getPart() {
        return fPart;
    }

    public IPreferenceStore getPreferenceStore() {
        return fStore;
    }

}
