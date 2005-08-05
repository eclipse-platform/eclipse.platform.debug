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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AsyncDebugView extends ViewPart {
    
    AsyncTreeViewer fViewer;

    public void createPartControl(Composite parent) {
        fViewer = new AsyncTreeViewer(parent);
        fViewer.setInput(DebugPlugin.getDefault().getLaunchManager());
        fViewer.setContext(new PresentationContext(this, null));
        
        DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
            public void handleDebugEvents(final DebugEvent[] events) {
                fViewer.getControl().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        for (int i = 0; i < events.length; i++) {
                            DebugEvent event = events[i];
                            switch (event.getKind()) {
                                case DebugEvent.RESUME:
                                    fViewer.update(event.getSource());
                                    break;
                                case DebugEvent.SUSPEND:
                                    Object source = event.getSource();
                                    if (source instanceof IThread) {
                                        IThread thread = (IThread) source;
                                        List path = new ArrayList();
                                        path.add(DebugPlugin.getDefault().getLaunchManager());
                                        path.add(thread.getLaunch());
                                        path.add(thread.getDebugTarget());
                                        path.add(thread);
                                        try {
                                            IStackFrame topStackFrame = thread.getTopStackFrame();
                                            if (topStackFrame != null) {
                                                path.add(topStackFrame);
                                                TreePath treePath = new TreePath(path.toArray());
                                                fViewer.expand(new TreeSelection(new TreePath[]{treePath}));
                                                fViewer.setSelection(new TreeSelection(new TreePath[]{treePath}));
                                            }
                                        } catch (DebugException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                default:
                                    fViewer.refresh(event.getSource());
                                    break;
                            }
                        }
                    }
                });
            }
        });
        
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchesListener() {
        
            public void launchesChanged(ILaunch[] launches) {
                refresh();
            }
        
            public void launchesAdded(ILaunch[] launches) {
                refresh();
            }
        
            public void launchesRemoved(ILaunch[] launches) {
                refresh();
            }
            
            private void refresh() {
                fViewer.refresh();
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
