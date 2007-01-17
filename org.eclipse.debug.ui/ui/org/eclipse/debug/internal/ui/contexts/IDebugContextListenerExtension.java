/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.contexts;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Optional extension to {@link org.eclipse.debug.internal.ui.contexts.provisional.IDebugContextListener}.
 * <p>
 * Temporary fix for bug 170519.
 * </p>
 * @since 3.2.2
 */
public interface IDebugContextListenerExtension {

	/**
	 * Notification an implicit evaluation completed.
	 * 
	 * @param selection context that completed the evaluation
	 * @param part part where context changed
	 */
	void contextImplicitEvaluationComplete(ISelection selection, IWorkbenchPart part);
}
