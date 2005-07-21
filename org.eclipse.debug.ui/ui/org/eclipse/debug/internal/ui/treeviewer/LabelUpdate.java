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
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class LabelUpdate extends AbstractUpdate implements ILabelUpdate {

    private String fText;
    private ImageDescriptor fImageDescriptor;

    public LabelUpdate(TreeItem item, AsyncTreeViewer viewer) {
        super(item, viewer);
    }

    protected void performUpdate() {
        if (fText != null) {
            getItem().setText(fText);
        }
        if (fImageDescriptor != null) {
            Image image = new Image(Display.getDefault(), fImageDescriptor
                    .getImageData());
            getItem().setImage(image);
        }
    }

    protected boolean contains(AbstractUpdate update) {
        return update instanceof LabelUpdate && update.getItem() == getItem();
    }

    public void setLabel(String text) {
        fText = text;
    }

    public void setFontData(FontData fontData) {
        // TODO Auto-generated method stub

    }

    public void setImageDescriptor(ImageDescriptor image) {
        fImageDescriptor = image;
    }

    public void setForeground(RGB foreground) {
        // TODO Auto-generated method stub

    }

    public void setBackground(RGB background) {
        // TODO Auto-generated method stub

    }

}
