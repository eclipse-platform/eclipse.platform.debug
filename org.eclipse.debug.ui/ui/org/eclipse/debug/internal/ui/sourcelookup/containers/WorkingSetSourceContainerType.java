/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.sourcelookup.containers;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainerTypeDelegate;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupUIMessages;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The factory for creating/restoring working set source containers.
 * 
 * @since 3.0
 */
public class WorkingSetSourceContainerType extends AbstractSourceContainerTypeDelegate {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainerTypeDelegate#getMemento(org.eclipse.debug.internal.core.sourcelookup.ISourceContainer)
	 */	
	public String getMemento(ISourceContainer container) throws CoreException {
		WorkingSetSourceContainer workingSet = (WorkingSetSourceContainer) container;
		Document doc = newDocument();		
		Element node = doc.createElement("workingSet"); //$NON-NLS-1$
		node.setAttribute("name", workingSet.getName()); //$NON-NLS-1$
		doc.appendChild(node);
		return serializeDocument(doc);	 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainerType#createSourceContainer(java.lang.String)
	 */
	public ISourceContainer createSourceContainer(String memento)throws CoreException {
		IWorkingSet workingSet = null;
		Exception ex = null;
		try {
			Element root = null;
			DocumentBuilder parser =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			StringReader reader = new StringReader(memento);
			InputSource source = new InputSource(reader);
			root = parser.parse(source).getDocumentElement();
			
			String name = root.getAttribute("setName");//$NON-NLS-1$
			
			if (isEmpty(name)) {
				abort(SourceLookupUIMessages.getString("sourceSearch.initError"),null);//$NON-NLS-1$
			}
			workingSet = WorkbenchPlugin.getDefault().getWorkingSetManager().getWorkingSet(name);
			//check that set still exists
			if (workingSet == null) {				
				abort(SourceLookupUIMessages.getString("sourceSearch.initError"), null); //$NON-NLS-1$
			}				
			return new WorkingSetSourceContainer(workingSet);	
			
		} catch (ParserConfigurationException e) {
			ex = e;
		} catch (SAXException e) {
			ex = e;
		} catch (IOException e) {
			ex = e;
		}
		
		abort(SourceLookupUIMessages.getString("sourceSearch.initError"), ex);	//$NON-NLS-1$
		return null;	
	}

	private boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
	
	
}
