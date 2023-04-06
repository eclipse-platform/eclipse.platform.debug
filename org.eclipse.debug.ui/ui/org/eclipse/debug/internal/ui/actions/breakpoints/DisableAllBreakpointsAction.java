/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.actions.breakpoints;

import org.eclipse.debug.internal.ui.actions.ActionMessages;

public class DisableAllBreakpointsAction extends AllBreakpointsEnablementAction {

	public DisableAllBreakpointsAction() {
		super(false, ActionMessages.DisableAllBreakPointsAction_0, ActionMessages.DisableAllBreakPointsAction_1);
	}
}
