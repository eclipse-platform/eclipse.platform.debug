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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Request to update the label of an element in a tree. 
 * <p>
 * Not intended to be subclassed or instantiated by clients. For use
 * speficially with <code>AsyncTreeViewer</code>.
 * </p>
 * @since 3.2
 */
class LabelUpdate extends AbstractUpdate implements ILabelUpdate {

	/**
	 * Retrieved label text. Only <code>null</code> if cancelled or failed.
	 */
    private String fText;
    
    /**
     * Retrieved image descriptor or <code>null</code>
     */
    private ImageDescriptor fImageDescriptor;
    
    /**
     * Retrieved font data or <code>null</code>
     */
    private FontData fFontData; 
    
    /**
     * Retieved colors or <code>null</code>
     */
    private RGB fForeground;
    private RGB fBackground;

    /**
     * Cosntructs a request to upate the label of the given widget in the
     * give viewer.
     * 
     * @param widget widget to update
     * @param viewer viewer containing the widget
     */
    public LabelUpdate(Widget widget, AsyncTreeViewer viewer) {
        super(widget, viewer);
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#performUpdate()
     */
    protected void performUpdate() {
        TreeItem item = (TreeItem) getWidget();
        if (fText != null) {
            item.setText(fText);
        }
        if (fImageDescriptor != null) {
        	Image image = getViewer().getImage(fImageDescriptor);
            item.setImage(image);
        }
        if (fFontData != null) {
        	Font font = getViewer().getFont(fFontData);
        		item.setFont(font);
        }
        if (fForeground != null) {
        	Color color = getViewer().getColor(fForeground);
        		item.setForeground(color);
        }
        if (fBackground != null) {
        	Color color = getViewer().getColor(fBackground);
        		item.setBackground(color);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate#contains(org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate)
     */
    protected boolean contains(AbstractUpdate update) {
        return update instanceof LabelUpdate && update.getWidget() == getWidget();
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate#setLabel(java.lang.String)
     */
    public void setLabel(String text) {
        fText = text;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate#setFontData(org.eclipse.swt.graphics.FontData)
     */
    public void setFontData(FontData fontData) {
        fFontData = fontData;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate#setImageDescriptor(org.eclipse.jface.resource.ImageDescriptor)
     */
    public void setImageDescriptor(ImageDescriptor image) {
        fImageDescriptor = image;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate#setForeground(org.eclipse.swt.graphics.RGB)
     */
    public void setForeground(RGB foreground) {
        fForeground = foreground;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.treeviewer.ILabelUpdate#setBackground(org.eclipse.swt.graphics.RGB)
     */
    public void setBackground(RGB background) {
        fBackground = background;
    }

}
