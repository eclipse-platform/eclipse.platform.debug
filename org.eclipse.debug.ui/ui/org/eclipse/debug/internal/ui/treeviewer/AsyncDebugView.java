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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AsyncDebugView extends ViewPart {
    
    AsyncTreeViewer fViewer;

    public void createPartControl(Composite parent) {
        fViewer = new AsyncTreeViewer(parent);
        fViewer.setInput(DebugPlugin.getDefault().getLaunchManager());
        
        DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
            public void handleDebugEvents(DebugEvent[] events) {
                fViewer.getControl().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        fViewer.refresh();
                    }
                });
            }
        });
        
    }

    public void setFocus() {
        fViewer.getControl().setFocus();
    }

    public void dispose() {
        fViewer.dispose();
        super.dispose();
    }
    
}
