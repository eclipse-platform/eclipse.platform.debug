/*******************************************************************************
 * Copyright (c) 2023 Simeon Andreev and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Simeon Andreev - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.actions.breakpoints;

import org.eclipse.debug.internal.ui.actions.ActionMessages;

public class EnableAllBreakpointsAction extends AllBreakpointsEnablementAction {

	public EnableAllBreakpointsAction() {
		super(true, ActionMessages.EnableAllBreakPointsAction_0, ActionMessages.EnableAllBreakPointsAction_1);
	}
}
