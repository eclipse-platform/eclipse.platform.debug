package org.eclipse.debug.internal.ui.views.variables;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.treeviewer.AbstractUpdate;
import org.eclipse.debug.internal.ui.treeviewer.AsyncTreeViewer;
import org.eclipse.debug.internal.ui.treeviewer.TreePath;
import org.eclipse.debug.internal.ui.treeviewer.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.progress.UIJob;

public class AsyncVariablesViewer extends AsyncTreeViewer{

	private VariablesView fView;

	public AsyncVariablesViewer(Composite parent, int style, VariablesView view) {
		super(parent, style);
		fView = view;
	}
	
	protected void toggleExpansion(TreePath path) {
		Object element = path.getLastSegment();
		TreePath[] treePaths = getTreePaths(element);
		for (int i = 0; i < treePaths.length; i++) {
			TreePath path2 = treePaths[i];
			if (path.equals(path2)) {
				TreeItem treeItem = getTreeItem(path2);
				TreeSelection selection = new TreeSelection(new TreePath[] { path2 });
				boolean expanded = treeItem.getExpanded();
				if (!expanded) {
					update(element);
					updateChildren(element, treeItem);
					expand(selection);
				} else {
					collapse(selection);
				}
			}
		}
	}

	
	protected void updateComplete(AbstractUpdate update) {
		super.updateComplete(update);
		UIJob restoreJob = new UIJob("restore viewer state") { //$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor) {
				fView.restoreState();
				return Status.OK_STATUS;
			}
		};
		restoreJob.setSystem(true);
		restoreJob.schedule(100);
	}
	
}
