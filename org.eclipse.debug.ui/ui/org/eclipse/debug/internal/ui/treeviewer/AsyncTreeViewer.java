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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

/**
 * A tree viewer that retrieves children and labels asynchronously via presentation
 * adapters and supports duplicate elements in the tree with different parents.
 * Retrieving children and labels asynchrnously allows for arbitrary latency without
 * blocking the UI thread. 
 * <p>
 * TODO: tree editor not implemented
 * 
 * TODO: default implementation of getting the image must run in UI thread, but
 * other implementations could run in non-UI thread
 * 
 * TODO: default presentation adapter should use deferred workbench adapters for
 * backwards compatibility - PROBLEM: we have to map an IElementCollector to
 * an IChildrenUpdate to make this work, but IElementCollector does not provide
 * information about whether a child can have children or not. First implementation
 * of this resulted in variables in the debug view (as children of stack frames)
 *
 * TODO: convert all JDT deferred workbench adapters to IPresentationAdapters
 * 
 * TODO: delete all of our deferred workbench adapters and our old RemoteTreeViewer code.
 * 
 * TODO: what to do about content provider and label provider as we use adpaters instead
 *  which are explicitly async
 * </p>
 * <p>
 * Clients may instantiate this class. Not intended to be subclassed.
 * </p>
 * @since 3.2
 */
public class AsyncTreeViewer extends StructuredViewer {

	/**
	 * A map of elements to associated tree items or tree
	 */
	private Map fElementsToWidgets = new HashMap();

	/**
	 * A map of widget to parent widgets used to avoid requirement for parent
	 * access in UI thread. Currently used by update objects to detect/cancel
	 * updates on updates of children.
	 */
	private Map fItemToParentItem = new HashMap();

	/**
	 * Map of widgets to their data elements used to avoid requirement to access
	 * data in UI thread.
	 */
	private Map fWidgetsToElements = new HashMap();

	/**
	 * List of updates currently being performed.
	 */
	private List fPendingUpdates = new ArrayList();

	/**
	 * Cache of images used for elements in this tree viewer. Label updates
	 * use the method <code>getImage(...)</code> to cache images for
	 * image descriptors. The images are disposed when this viewer is disposed.
	 */
	private Map fImageCache = new HashMap();

	/**
	 * Cache of the fonts used for elements in this tree viewer. Label updates
	 * use the method <code>getFont(...)</code> to cache fonts for
	 * FontData objects. The fonts are disposed with the viewer.
	 */
	private Map fFontCache = new HashMap();

	/**
	 * Cache of the colors used for elements in this tree viewer. Label updates
	 * use the method <code>getColor(...)</code> to cache colors for
	 * RGB values. The colors are disposed with the viewer.
	 */
	private Map fColorCache = new HashMap();
	
	/**
	 * The tree
	 */
	private Tree fTree;

	/**
	 * The root element/input to the viewer
	 */
	private Object fInput;

	/**
	 * The context in which this viewer is being used - i.e. what part it is contained
	 * in any any preference settings associated with it.
	 */
	private IPresentationContext fContext;

	TreeSelection fPendingSelection;

	TreeSelection fCurrentSelection;

	/**
	 * Array of tree paths to be expanded. As paths are expanded, those
	 * entries are set to <code>null</code>.
	 */
	TreePath[] fPendingExpansion;

	/**
	 * Creates an asynchronous tree viewer on a newly-created tree control under
	 * the given parent. The tree control is created using the SWT style bits
	 * <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public AsyncTreeViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	/**
	 * Creates an asynchronous tree viewer on a newly-created tree control under
	 * the given parent. The tree control is created using the given SWT style
	 * bits. The viewer has no input.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the SWT style bits used to create the tree.
	 */
	public AsyncTreeViewer(Composite parent, int style) {
		this(new Tree(parent, style));
	}

	/**
	 * Creates an asynchronous tree viewer on the given tree control. The viewer
	 * has no input, no content provider, a default label provider, no sorter,
	 * and no filters.
	 * 
	 * @param tree
	 *            the tree control
	 */
	public AsyncTreeViewer(Tree tree) {
		super();
		fTree = tree;
		hookControl(fTree);
		setUseHashlookup(false);
		setContentProvider(new NullContentProvider());
		tree.addTreeListener(new TreeListener() {
			public void treeExpanded(TreeEvent e) {
				((TreeItem)e.item).setExpanded(true);
				internalRefresh(e.item.getData(), e.item);
			}

			public void treeCollapsed(TreeEvent e) {
			}
		});
		tree.addMouseListener(new MouseListener() {
		
			public void mouseUp(MouseEvent e) {
			}
		
			public void mouseDown(MouseEvent e) {
			}
		
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem item = ((Tree)e.widget).getItem(new Point(e.x, e.y));
				if (item.getExpanded()) {
					item.setExpanded(false);
				} else {
					item.setExpanded(true);
					internalRefresh(item.getData(), item);
				}
			}
		});
	}

	/**
	 * Returns the tree control for this viewer.
	 * 
	 * @return the tree control for this viewer
	 */
	public Tree getTree() {
		return fTree;
	}
	
	/**
	 * Clients must call this methods when this viewer is no longer needed
	 * so it can perform cleanup.
	 */
	public void dispose() {
		Iterator images = fImageCache.values().iterator();
		while (images.hasNext()) {
			Image image = (Image) images.next();
			image.dispose();
		}
		
		Iterator fonts = fFontCache.values().iterator();
		while (fonts.hasNext()) {
			Font font = (Font) fonts.next();
			font.dispose();
		}
		
		Iterator colors = fColorCache.values().iterator();
		while (colors.hasNext()) {
			Color color = (Color) colors.next();
			color.dispose();
		}
		
		fElementsToWidgets.clear();
		fPendingUpdates.clear();
	}

	/**
	 * Updates all occurrences of the given element in this tree.
	 * 
	 * @param element element to update
	 */
	public void update(Object element) {
		if (element == fInput) {
			return; // the root is not displayed
		}
		Widget[] items = getWidgets(element);
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				update(element, items[i]);
			}
		}
	}
	
	/**
	 * Updates the label for a specific element and item.
	 * 
	 * @param element element to update
	 * @param item its associated item
	 */
	protected void update(Object element, Widget item) {
		if (item instanceof TreeItem) {
			IPresentationAdapter adapter = getPresentationAdapter(element);
			if (adapter != null) {
				ILabelUpdate labelUpdate = new LabelUpdate(item, this);
				schedule(labelUpdate);
				adapter.retrieveLabel(element, fContext, labelUpdate);
			}
		}
	}

	/**
	 * Refreshes all occurrences of the given element in this tree, and visible
	 * children.
	 * 
	 * @param element element to refresh
	 */
	public void refresh(Object element) {
		internalRefresh(element);
	}

	/**
	 * Updates the children of the given element.
	 * 
	 * @param parent element of which to update children
	 * @param widget widget associated with the element in this viewer's tree  
	 */
	protected void updateChildren(Object parent, Widget widget) {
		if (parent == fInput || ((TreeItem)widget).getExpanded()) {
			IPresentationAdapter adapter = getPresentationAdapter(parent);
			if (adapter != null) {
				IChildrenUpdate updateChildren = new ChildrenUpdate(widget, this);
				schedule(updateChildren);
				adapter.retrieveChildren(parent, fContext, updateChildren);
			}
		}
	}

	/**
	 * Returns the presentation adapter for the given element or <code>null</code> if none.
	 * 
	 * @param element element to retrieve adapter for
	 * @return presentation adapter or <code>null</code>
	 * 
	 * TODO: needs to be revisited
	 */
	protected IPresentationAdapter getPresentationAdapter(Object element) {
		IPresentationAdapter adapter = null;
		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			adapter = (IPresentationAdapter) adaptable.getAdapter(IPresentationAdapter.class);
			
			if (adapter == null) {
				IDeferredWorkbenchAdapter deferredWorkbenchAdapter = (IDeferredWorkbenchAdapter) adaptable.getAdapter(IDeferredWorkbenchAdapter.class);
				adapter = new DeferredWorkbenchPresentationAdapter(deferredWorkbenchAdapter);
			}
		}
		return adapter;
	}

	/**
	 * Cancels any conflicting updates for children of the given item, and
	 * schedules the new update.
	 * 
	 * @param update the update to schedule
	 */
	protected void schedule(IPresentationUpdate update) {
		AbstractUpdate absUpdate = (AbstractUpdate) update;
		synchronized (fPendingUpdates) {
			Iterator updates = fPendingUpdates.listIterator();
			while (updates.hasNext()) {
				AbstractUpdate pendingUpdate = (AbstractUpdate) updates.next();
				if (absUpdate.contains(pendingUpdate)) {
					pendingUpdate.setCanceled(true);
					updates.remove();
				}
			}
			fPendingUpdates.add(update);
		}
	}

	/**
	 * Returns the widgets associated with the given element or
	 * <code>null</code>.
	 * 
	 * @param element element to retrieve widgets for
	 * @return widgets or <code>null</code> if none
	 */
	protected synchronized Widget[] getWidgets(Object element) {
		if (element == null) {
			return null;
		}
		return (Widget[]) fElementsToWidgets.get(element);
	}
	
	/**
	 * Expands all elements in the given tree selection.
	 * 
	 * @param selection
	 */
	public synchronized void expand(ISelection selection) {
		if (selection instanceof TreeSelection) {
			fPendingExpansion = ((TreeSelection) selection).getPaths();
			attemptExpansion();
		}
	}

	/**
	 * Attempts to expand all pending expansions.
	 */
	synchronized void attemptExpansion() {
		if (fPendingExpansion != null) {
			for (int i = 0; i < fPendingExpansion.length; i++) {
				TreePath path = fPendingExpansion[i];
				if (path != null && attemptExpansion(path)) {
					fPendingExpansion[i] = null;
				}
			}
		}
	}

	/**
	 * Attempts to expand the given tree path and returns whether the
	 * expansion was completed.
	 * 
	 * @param path path to exapand
	 * @return whether the expansion was completed
	 */
	synchronized boolean attemptExpansion(TreePath path) {
		int segmentCount = path.getSegmentCount();
		for (int j = segmentCount - 1; j >= 0; j--) {
			Object element = path.getSegment(j);
			Widget[] treeItems = (Widget[]) fElementsToWidgets.get(element);
			if (treeItems != null) {
				for (int k = 0; k < treeItems.length; k++) {
					if (treeItems[k] instanceof TreeItem) {
						TreeItem treeItem = (TreeItem) treeItems[k];
						TreePath treePath = getTreePath(treeItem);
						if (path.startsWith(treePath)) {
							if (!treeItem.getExpanded()) {
								expand(treeItem);
								update(element);
								updateChildren(element, treeItem);
								if (path.getSegmentCount() == treePath.getSegmentCount()) {
									return true;
								}
								return false;
							}
						}
					} 
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return fTree;
	}

	
	/**
	 * Clears all element/widget caches in the tree. Called when the input is reset.
	 */
	protected synchronized void unmapAllElements() {
		Iterator iterator = fElementsToWidgets.keySet().iterator();
		while (iterator.hasNext()) {
			Object element = iterator.next();
			Widget[] widgets = getWidgets(element);
			if (widgets != null) {
				for (int i = 0; i < widgets.length; i++) {
					Widget widget = widgets[i];
					if (widget instanceof TreeItem) {
						TreeItem item = (TreeItem) widget;
						item.dispose();
					}
				}
			}
		}
		fElementsToWidgets.clear();
		fItemToParentItem.clear();
		fWidgetsToElements.clear();
	}

	/**
	 * Cancels all pending update requests.
	 */
	protected synchronized void cancelPendingUpdates() {
		Iterator updates = fPendingUpdates.iterator();
		while (updates.hasNext()) {
			IPresentationUpdate update = (IPresentationUpdate) updates.next();
			update.setCanceled(true);
		}
		fPendingUpdates.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	protected void inputChanged(Object input, Object oldInput) {
		cancelPendingUpdates();
		unmapAllElements();
		fInput = input;

		map(input, fTree);
		refresh();
	}

	/**
	 * Maps the given element to the given item.
	 * 
	 * @param element model element
	 * @param item TreeItem or Tree
	 */
	protected void map(Object element, Widget item) {
		item.setData(element);
		Object object = fElementsToWidgets.get(element);
		fWidgetsToElements.put(item, element);
		if (object == null) {
			fElementsToWidgets.put(element, new Widget[] { item });
		} else {
			Widget[] old = (Widget[]) object;
			Widget[] items = new Widget[old.length + 1];
			System.arraycopy(old, 0, items, 0, old.length);
			items[old.length] = item;
			fElementsToWidgets.put(element, items);
		}
		if (item instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) item;
			TreeItem parentItem = treeItem.getParentItem();
			if (parentItem != null) {
				fItemToParentItem.put(treeItem, parentItem);
			}
		}
	}

	/**
	 * Returns all paths to the given element or <code>null</code> if none.
	 * 
	 * @param element
	 * @return paths to the given element or <code>null</code>
	 */
	public synchronized TreePath[] getTreePaths(Object element) {
		Widget[] widgets = getWidgets(element);
		if (widgets == null) {
			return null;
		}
		TreePath[] paths = new TreePath[widgets.length];
		for (int i = 0; i < widgets.length; i++) {
			List path = new ArrayList();
			path.add(element);
			Widget widget = widgets[i];
			TreeItem parent = null;
			if (widget instanceof TreeItem) {
				TreeItem treeItem = (TreeItem) widget;
				parent = getParentItem(treeItem);
			}
			while (parent != null) {
				Object data = fWidgetsToElements.get(parent);
				path.add(0, data);
				parent = getParentItem(parent);
			}
			path.add(0, fInput);
			paths[i] = new TreePath(path.toArray());
			if (widget instanceof TreeItem) {
				paths[i].setTreeItem((TreeItem) widget);
			}
		}
		return paths;
	}

	/**
	 * Constructs and returns a tree path for the given item. Must be called from the
	 * UI thread.
	 * 
	 * @param item item to constuct a path for
	 * @return tree path for the item
	 */
	protected TreePath getTreePath(TreeItem item) {
		TreeItem parent = item;
		List path = new ArrayList();
		while (parent != null) {
			path.add(0, parent.getData());
			parent = parent.getParentItem();
		}
		path.add(0, fTree.getData());
		return new TreePath(path.toArray());
	}

	/**
	 * Removes the update from the pending updates list.
	 * 
	 * @param update
	 */
	protected void updateComplete(IPresentationUpdate update) {
		synchronized (fPendingUpdates) {
			fPendingUpdates.remove(update);
		}
	}

	/**
	 * Called by <code>ChildrenUpdate</code> after children have been retrieved.
	 * 
	 * @param widget
	 * @param newChildren
	 * @param hasChildren
	 */
	synchronized void setChildren(Widget widget, List newChildren, List hasChildren) {
		//apply filters
		Object[] children = filter(newChildren.toArray());
		
		//sort filtered children
		ViewerSorter viewerSorter = getSorter();
		if (viewerSorter != null) {
			viewerSorter.sort(this, children);
		}
		
		//update tree
		TreeItem[] oldItems = null;
		if (widget instanceof Tree) {
			Tree tree = (Tree) widget;
			oldItems = tree.getItems();
		} else {
			oldItems = ((TreeItem) widget).getItems();
		}
		
		int index = 0;
		for (; index < children.length; index++) {
			Object kid = children[index];
			boolean hasKids = ((Boolean) hasChildren.get(index)).booleanValue();
			if (index < oldItems.length) {
				TreeItem oldItem = oldItems[index];
				Object oldData = oldItem.getData();
				if (!kid.equals(oldData)) {
					unmap(oldData, oldItem);
					map(kid, oldItem);
				}
				if (!hasKids && oldItem.getItemCount() > 0) {
					// dispose children
					TreeItem[] items = oldItem.getItems();
					for (int i = 0; i < items.length; i++) {
						TreeItem oldChild = items[i];
						unmap(oldChild.getData(), oldChild);
						oldChild.dispose();
					}
				} else if (hasKids && oldItem.getItemCount() == 0) {
					// dummy to update +
					new TreeItem(oldItem, SWT.NONE);
				}
			} else {
				TreeItem newItem = newTreeItem(widget, index);
				map(kid, newItem);
				if (hasKids) {
					// dummy to update +
					new TreeItem(newItem, SWT.NONE);
				}
			}
		}
		// remove left over old items
		while (index < oldItems.length) {
			TreeItem oldItem = oldItems[index];
			unmap(oldItem.getData(), oldItem);
			oldItem.dispose();
			index++;
		}
		// refresh the current kids
		for (int i = 0; i < children.length; i++) {
			refresh(children[i]);
		}
		
		attemptExpansion();
		attemptSelection(true);
	}
	
	/**
	 * Expands the given tree item and all of its parents. Does *not* update elements
	 * or retrieve children.
	 * 
	 * @param child item to expand
	 */
    private void expand(TreeItem child) {
    	if (!child.getExpanded()) {
			child.setExpanded(true);
			TreeItem parent = child.getParentItem();
			if (parent != null) {
				expand(parent);
			}
    	}
	}

    /**
     * Creates a new tree item as a child of the given widget at the
     * specified index.
     * 
     * @param parent parent widget - a Tree or TreeItem
     * @param index index at which to create new child
     * @return tree item
     */
	protected TreeItem newTreeItem(Widget parent, int index) {
		if (parent instanceof Tree) {
			return new TreeItem((Tree) parent, SWT.NONE, index);
		}
		return new TreeItem((TreeItem) parent, SWT.NONE, index);
	}

	/**
	 * Unmaps the given item, and unmaps and disposes of all children of that
	 * item. Does not dispose of the given item.
	 * 
	 * @param kid
	 * @param oldItem
	 */
	protected synchronized void unmap(Object kid, TreeItem oldItem) {
		if (kid == null) {
			// when unmapping a dummy item
			return;
		}
		Widget[] widgets = (Widget[]) fElementsToWidgets.get(kid);
		fWidgetsToElements.remove(oldItem);
		if (widgets != null) {
			for (int i = 0; i < widgets.length; i++) {
				Widget item = widgets[i];
				if (item == oldItem) {
					fItemToParentItem.remove(item);
					if (widgets.length == 1) {
						fElementsToWidgets.remove(kid);
					} else {
						Widget[] newItems = new Widget[widgets.length - 1];
						System.arraycopy(widgets, 0, newItems, 0, i);
						if (i < newItems.length) {
							System.arraycopy(widgets, i + 1, newItems, i, newItems.length - i);
						}
						fElementsToWidgets.put(kid, newItems);
					}
				}
			}
		}
		TreeItem[] children = oldItem.getItems();
		for (int i = 0; i < children.length; i++) {
			TreeItem child = children[i];
			unmap(child.getData(), child);
			child.dispose();
		}
	}

	/**
	 * Returns an image for the given image descriptor. Adds the image to a cache
	 * of images if it does not already exist. The cache is cleared when this viewer
	 * is disposed. 
	 * 
	 * @param descriptor image descriptor
	 * @return image
	 */
	protected Image getImage(ImageDescriptor descriptor) {
		Image image = (Image) fImageCache.get(descriptor);
		if (image == null) {
			image = new Image(getControl().getDisplay(), descriptor.getImageData());
			fImageCache.put(descriptor, image);
		}
		return image;
	}

	public Font getFont(FontData fontData) {
		Font font = (Font) fFontCache.get(fontData);
		if (font == null) {
			font = new Font(getControl().getDisplay(), fontData);
			fFontCache.put(fontData, font);
		}
		return font;
	}
	
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorCache.get(rgb);
		if (color == null) {
			color = new Color(getControl().getDisplay(), rgb);
			fColorCache.put(rgb, color);
		}
		return color;
	}
	
	/**
	 * Sets the context for this viewer. 
	 * 
	 * @param context
	 * 
	 * TODO: needed? replace with IWorkbenchPart?
	 */
	public void setContext(IPresentationContext context) {
		fContext = context;
	}

	/**
	 * Returns the parent item for an item or <code>null</code> if none.
	 * 
	 * @param item
	 *            item for which parent is requested
	 * @return parent item or <code>null</code>
	 */
	protected synchronized TreeItem getParentItem(TreeItem item) {
		return (TreeItem) fItemToParentItem.get(item);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindInputItem(java.lang.Object)
	 */
	protected Widget doFindInputItem(Object element) {
		if (element.equals(fInput)) {
			return fTree;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindItem(java.lang.Object)
	 */
	protected Widget doFindItem(Object element) {
		Widget[] widgets = getWidgets(element);
		if (widgets != null && widgets.length > 0) {
			return widgets[0];
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#doUpdateItem(org.eclipse.swt.widgets.Widget, java.lang.Object, boolean)
	 */
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		update(element, item);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		Control control = getControl();
		if (control == null || control.isDisposed()) {
			return StructuredSelection.EMPTY;
		}
		List list = getSelectionFromWidget();
		return new TreeSelection((TreePath[]) list.toArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	protected List getSelectionFromWidget() {
		TreeItem[] selection = fTree.getSelection();
		TreePath[] paths = new TreePath[selection.length];
		for (int i = 0; i < selection.length; i++) {
			paths[i] = getTreePath(selection[i]);
		}
		return Arrays.asList(paths);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.Object)
	 */
	protected void internalRefresh(Object element) {
		Widget[] items = getWidgets(element);
		if (items == null) {
			return;
		}
		for (int i = 0; i < items.length; i++) {
			internalRefresh(element, items[i]);
		}
	}
	
	/**
	 * Refreshes a specific occurrence of an element.
	 * 
	 * @param element element to update
	 * @param item item to update
	 */
	protected void internalRefresh(Object element, Widget item) {
		update(element, item);
		updateChildren(element, item);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#reveal(java.lang.Object)
	 */
	public void reveal(Object element) {
		Widget[] widgets = getWidgets(element);
		if (widgets != null && widgets.length > 0) {
			// TODO: only reveals the first occurrence - should we reveal all?
			TreeItem item = (TreeItem) widgets[0];
			Tree tree = (Tree) getControl();
			tree.showItem(item);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(org.eclipse.jface.viewers.ISelection, boolean)
	 */
	protected void setSelectionToWidget(ISelection selection, boolean reveal) {
		if (selection instanceof TreeSelection) {
			setSelectionToWidget((TreeSelection) selection, reveal);
		} else {
			super.setSelectionToWidget(selection, reveal);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	protected void setSelectionToWidget(List list, boolean reveal) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object selection = iter.next();
			if (selection instanceof TreeSelection) {
				setSelectionToWidget((TreeSelection) selection, reveal);
			}
		}
	}

	/**
	 * Schedules a pending selection update if the specified selection is not the
	 * same as the current selection.
	 * 
	 * @param selection new selection
	 * @param reveal wether to reveal the selection
	 */
	protected void setSelectionToWidget(TreeSelection selection, final boolean reveal) {
		// check if same
		if (fCurrentSelection != null) {
			if (fCurrentSelection.equals(selection) && selection.equals(getSelection())) {
				return;
			}
			fCurrentSelection = null;
		}
		fPendingSelection = selection;
		fTree.getDisplay().asyncExec(new Runnable() {
			public void run() {
				attemptSelection(reveal);
			}
		});
		
	}

	/**
	 * Attempts to select elements in the pending selection.
	 * 
	 * @param reveal whether to reveal items in the selection
	 */
	synchronized void attemptSelection(final boolean reveal) {
		if (fPendingSelection != null) {
			List remaining = new ArrayList();
			List toSelect = new ArrayList();
			TreePath[] paths = fPendingSelection.getPaths();
			if (paths == null) {
				return;
			}
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				if (path == null) {
					continue; 
				}
				TreePath[] treePaths = getTreePaths(path.getLastSegment());
				boolean selected = false;
				if (treePaths != null) {
					for (int j = 0; j < treePaths.length; j++) {
						TreePath existingPath = treePaths[j];
						if (existingPath.equals(path)) {
							toSelect.add(existingPath.getTreeItem());
							selected = true;
							break;
						}
					}
				}
				if (!selected) {
					remaining.add(path);
				}
			}
			if (remaining.isEmpty()) {
				// done
				fPendingSelection = null;
			} else {
				fPendingSelection = new TreeSelection((TreePath[]) remaining.toArray());
			}
			if (!toSelect.isEmpty()) {
				final TreeItem[] items = (TreeItem[]) toSelect.toArray(new TreeItem[toSelect.size()]);

				fTree.setSelection(items);
				if (reveal) {
					fTree.showItem(items[0]);
				}
				fCurrentSelection = (TreeSelection) getSelection();
				fireSelectionChanged(new SelectionChangedEvent(AsyncTreeViewer.this, fCurrentSelection));
			}
		}
	}

	/**
	 * A content provider that does nothing.
	 * 
	 * TODO: needs to be revisited
	 */
	private class NullContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			return null;
		}
	}

	/**
	 * Collapses all items in the tree.
	 */
	public void collapseAll() {
		TreeItem[] items = fTree.getItems();
		for (int i = 0; i < items.length; i++) {
			TreeItem item = items[i];
			if (item.getExpanded())
				collapse(item);
		}
	}

	/**
	 * Collaspes the given item and all of its children items.
	 * 
	 * @param item item to collapose recursively
	 */
	protected void collapse(TreeItem item) {
		TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			TreeItem child = items[i];
			if (child.getExpanded()) {
				collapse(child);
			}
		}
		item.setExpanded(false);
	}
}
