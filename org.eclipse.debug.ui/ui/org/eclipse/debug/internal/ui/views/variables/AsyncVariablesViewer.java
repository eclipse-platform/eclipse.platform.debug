package org.eclipse.debug.internal.ui.views.variables;

import org.eclipse.debug.internal.ui.treeviewer.AsyncTreeViewer;
import org.eclipse.debug.internal.ui.treeviewer.TreePath;
import org.eclipse.debug.internal.ui.treeviewer.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

public class AsyncVariablesViewer extends AsyncTreeViewer{

	public AsyncVariablesViewer(Composite parent, int style) {
		super(parent, style);
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


}
