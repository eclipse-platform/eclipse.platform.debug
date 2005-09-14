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

/**
 * A presentation monitor that collects attributes of a presentation adapter's
 * label.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.2
 */
public interface ILabelRequestMonitor extends IPresentationRequestMonitor {

	/**
	 * Sets the text of the label. Cannot be <code>null</code>.
	 * 
	 * @param text
	 */
    public void setLabel(String text);
    
    /**
     * Sets the font of the label.
     * 
     * @param fontData
     */
    public void setFontData(FontData fontData);
    
    /**
     * Sets the image of the label.
     * 
     * @param image
     */
    public void setImageDescriptor(ImageDescriptor image);
    
    /**
     * Sets the foreground color of the label.
     * 
     * @param foreground
     */
    public void setForeground(RGB foreground);
    
    /**
     * Sets the background color of the label.
     * 
     * @param background
     */
    public void setBackground(RGB background);

}
