package org.eclipse.ui.externaltools.internal.ui;

import org.eclipse.jdt.internal.debug.ui.actions.OpenOnConsoleTypeAction;
import org.eclipse.jface.text.IDocument;

/**
 */
public class OpenOnLogConsoleTypeAction extends OpenOnConsoleTypeAction {
	protected IDocument getConsoleDocument() {
		LogConsoleView lv = (LogConsoleView)getViewPart();
		if (lv != null)
			return lv.getTextViewer().getDocument();
		return null;		
	}
}
