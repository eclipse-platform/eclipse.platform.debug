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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.internal.ui.DelegatingModelPresentation;
import org.eclipse.debug.internal.ui.LazyModelPresentation;
import org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IExpandableUpdate;
import org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter;
import org.eclipse.debug.internal.ui.treeviewer.IPresentationContext;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.ui.IWorkbenchPart;

public abstract class AbstractAsyncPresentationAdapter implements IPresentationAdapter {
	
	protected static final Object[] EMPTY = new Object[0];
	
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter#retrieveChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext, org.eclipse.debug.internal.ui.treeviewer.IChildrenUpdate)
     */
    public void retrieveChildren(final Object parent, final IPresentationContext context, final IChildrenUpdate result) {
		Job job = new Job("Retrieving Children") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				IStatus status = Status.OK_STATUS;
				try {
					result.addChildren(getChildren(parent, context));
				} catch (CoreException e) {
					status = e.getStatus();
				}
				result.setStatus(status);
				result.done();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter#hasChildren(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext, org.eclipse.debug.internal.ui.treeviewer.IExpandableUpdate)
     */
    public void hasChildren(final Object element, final IPresentationContext context, final IExpandableUpdate result) {
    	Job job = new Job("Computing hasChildren") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				IStatus status = Status.OK_STATUS;
				try {
					result.hasChildren(hasChildren(element, context));
				} catch (CoreException e) {
					status = e.getStatus();
				}
				result.setStatus(status);
				result.done();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
		
	}
        
    /**
     * Returns the children for the given parent in the specified context.
     * 
     * @param parent element to retrieve children for
     * @param context context children will be presented in
     * @return children
     * @throws CoreException if an exception occurrs retieving children
     */
    protected abstract Object[] getChildren(Object parent, IPresentationContext context) throws CoreException;
    
    /**
     * Returns whether the given element has children in the specified context.
     * 
     * @param element element that may have children
     * @param context context element will be presented in
     * @return whether the given element has children in the specified context
     * @throws CoreException if an exception occurrs determining whether the
     *  element has children
     */
    protected abstract boolean hasChildren(Object element, IPresentationContext context) throws CoreException;    
  
    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.IPresentationAdapter#retrieveLabel(java.lang.Object, org.eclipse.debug.internal.ui.treeviewer.IPresentationContext, org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate)
     */
    public void retrieveLabel(final Object object, final IPresentationContext context, final ILabelUpdate result) {
    	// Default implementation does not run in the UI thread. Clients should override with
    	// UI job if required
        Job job = new Job("Retrieving labels") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor monitor) {
                return doRetrieveLabel(object, context, result);
            }
        };
        job.setSystem(true);
        job.schedule();
    }
    
    protected IStatus doRetrieveLabel (Object object, IPresentationContext context, ILabelUpdate result) {
    	DelegatingModelPresentation presentation = DebugElementHelper.getPresentation();
    	// Honor view specific settings in a debug view by copying model presentation settings
    	// into the debug element helper's presentation before we get the label. This allows
    	// for qualified name and type name settings to remain in tact.
    	if (object instanceof IDebugElement && context.getPart() instanceof IDebugView) {
    		IWorkbenchPart part = context.getPart();
    		if (part instanceof IDebugView) {
    			IDebugModelPresentation pres = ((IDebugView)part).getPresentation(((IDebugElement)object).getModelIdentifier());
    			Map settings = null;
	    		synchronized (presentation) {
	    			if (pres instanceof DelegatingModelPresentation) {
	    				settings = ((DelegatingModelPresentation)pres).getAttributes();
	    			} else if (pres instanceof LazyModelPresentation) {
	    				settings = ((LazyModelPresentation)pres).getAttributes();
	    			}
	    			if (settings != null) {
			    		Iterator iterator = settings.entrySet().iterator();
			    		while (iterator.hasNext()) {
			    			Map.Entry entry = (Entry) iterator.next();
			    			presentation.setAttribute((String) entry.getKey(), entry.getValue());
			    		}
			        	result.setLabel(DebugElementHelper.getLabel(object));
			            result.setImageDescriptor(DebugElementHelper.getImageDescriptor(object));
			            result.setFontData(DebugElementHelper.getFont(object));
			            result.setBackground(DebugElementHelper.getBackground(object));
			            result.setForeground(DebugElementHelper.getForeground(object));
			            result.done();
			            return Status.OK_STATUS;  
	    			}
	    		}
	    	}
		}
    	result.setLabel(DebugElementHelper.getLabel(object));
        result.setImageDescriptor(DebugElementHelper.getImageDescriptor(object));
        result.setFontData(DebugElementHelper.getFont(object));
        result.setBackground(DebugElementHelper.getBackground(object));
        result.setForeground(DebugElementHelper.getForeground(object));
        result.done();
        return Status.OK_STATUS;        
    }

}
