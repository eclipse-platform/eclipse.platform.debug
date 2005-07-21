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
import org.eclipse.swt.graphics.RGB;

public interface ILabelUpdate extends IPresentationUpdate {

    public void setLabel(String text);
    
    public void setFontData(FontData fontData);
    
    public void setImageDescriptor(ImageDescriptor image);
    
    public void setForeground(RGB foreground);
    
    public void setBackground(RGB background);

}
