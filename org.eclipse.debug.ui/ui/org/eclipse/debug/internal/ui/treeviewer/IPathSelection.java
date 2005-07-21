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

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;

public interface IPathSelection extends IStructuredSelection {
    
    /**
     * Returns an iterator containing the paths to selected elements
     * in an async tree viewer. Each element in the iterator is an
     * array of Objects representing a path to selected elements.
     * 
     * @return
     */
    public Iterator pathIterator();

}
