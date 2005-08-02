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

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class AsyncVariablesView extends ViewPart implements ISelectionListener {

    private AsyncTreeViewer fViewer;

    public void createPartControl(Composite parent) {
        fViewer = new AsyncTreeViewer(parent);

        IPresentationContext context = new IPresentationContext() {
            public IWorkbenchPart getPart() {
                return AsyncVariablesView.this;
            }

            public IPreferenceStore getPreferenceStore() {
                return null;
            }
        };
        fViewer.setContext(context);

        getSite().getPage().addSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, this);

    }

    public void setFocus() {
        // TODO Auto-generated method stub

    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        Object element = ((IStructuredSelection) selection).getFirstElement();
        if (element instanceof IStackFrame) {
            if (element.equals(fViewer.getInput())) {
                fViewer.refresh();
            } else {
                fViewer.setInput(element);
            }
        } else {
            fViewer.setInput(null);
        }
    }

}
