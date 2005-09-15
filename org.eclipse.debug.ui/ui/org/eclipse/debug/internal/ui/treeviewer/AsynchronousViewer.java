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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * @since 3.2
 */
public abstract class AsynchronousViewer extends StructuredViewer {

	/**
	 * A map of elements to associated tree items or tree
	 */
	private Map fElementsToWidgets = new HashMap();

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
	 * The context in which this viewer is being used - i.e. what part it is contained
	 * in any any preference settings associated with it.
	 */
	private IPresentationContext fContext;

	private ISelection fPendingSelection;

	private ISelection fCurrentSelection;

	/**
	 * Creates a presentation adapter viewer 
	 */
	protected AsynchronousViewer() {
		setContentProvider(new NullContentProvider());
	}
	
	/**
	 * Clients must call this methods when this viewer is no longer needed
	 * so it can perform cleanup.
	 */
	public synchronized void dispose() {
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
		
		unmapAllElements();
		fPendingUpdates.clear();
	}

	/**
	 * Updates all occurrences of the given element in this viewer.
	 * 
	 * @param element element to update
	 */
	public void update(Object element) {
		if (element == getInput()) {
			return; // the input is not displayed
		}
		Widget[] items = getWidgets(element);
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				updateLabel(element, items[i]);
			}
		}
	}
	
	/**
	 * Updates the label for a specific element and item.
	 * 
	 * @param element element to update
	 * @param item its associated item
	 */
	protected void updateLabel(Object element, Widget item) {
		if (item instanceof Item) {
			IAsynchronousLabelAdapter adapter = getLabelAdapter(element);
			if (adapter != null) {
				ILabelRequestMonitor labelUpdate = new LabelRequestMonitor(item, this);
				schedule(labelUpdate);
				adapter.retrieveLabel(element, getPresentationContext(), labelUpdate);
			}
		}
	}
		
	/**
	 * Returns the presentation context to be used in update requests.
	 * Clients may override this method if required to provide special
	 * implementations of contexts.
	 * 
	 * @return presentation contenxt
	 */
	protected IPresentationContext getPresentationContext() {
		return fContext;
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
	 * Returns the label adapter for the given element or <code>null</code> if none.
	 * 
	 * @param element element to retrieve adapter for
	 * @return presentation adapter or <code>null</code>
	 */
	protected IAsynchronousLabelAdapter getLabelAdapter(Object element) {
		IAsynchronousLabelAdapter adapter = null;
		if (element instanceof IAsynchronousLabelAdapter) {
			adapter = (IAsynchronousLabelAdapter) element;
		} else if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			adapter = (IAsynchronousLabelAdapter) adaptable.getAdapter(IAsynchronousLabelAdapter.class);
		}
		return adapter;
	}	
	
	/**
	 * Cancels any conflicting updates for children of the given item, and
	 * schedules the new update.
	 * 
	 * @param update the update to schedule
	 */
	protected void schedule(IPresentationRequestMonitor update) {
		PresentationRequestMonitor absUpdate = (PresentationRequestMonitor) update;
		synchronized (fPendingUpdates) {
			Iterator updates = fPendingUpdates.listIterator();
			while (updates.hasNext()) {
				PresentationRequestMonitor pendingUpdate = (PresentationRequestMonitor) updates.next();
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
	 * Returns the element associated with the given widget or
	 * <code>null</code>.
	 * 
	 * @param widget widget to retrieve element for
	 * @return element or <code>null</code> if none
	 */
	protected synchronized Object getElement(Widget widget) {
		return fWidgetsToElements.get(widget);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#unmapAllElements()
	 */
	protected synchronized void unmapAllElements() {
		Iterator iterator = fElementsToWidgets.keySet().iterator();
		while (iterator.hasNext()) {
			Object element = iterator.next();
			Widget[] widgets = getWidgets(element);
			if (widgets != null) {
				for (int i = 0; i < widgets.length; i++) {
					Widget widget = widgets[i];
					if (widget instanceof Item) {
						Item item = (Item) widget;
						item.dispose();
					}
				}
			}
		}
		fElementsToWidgets.clear();
		fWidgetsToElements.clear();
	}

	/**
	 * Cancels all pending update requests.
	 */
	protected synchronized void cancelPendingUpdates() {
		Iterator updates = fPendingUpdates.iterator();
		while (updates.hasNext()) {
			IPresentationRequestMonitor update = (IPresentationRequestMonitor) updates.next();
			update.setCanceled(true);
		}
		fPendingUpdates.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	protected void inputChanged(Object input, Object oldInput) {
		cancelPendingUpdates();
	}

	/**
	 * Maps the given element to the given item.
	 * 
	 * @param element model element
	 * @param item TreeItem or Tree
	 */
	protected void map(Object element, Widget item) {
		item.setData(element);
		Widget[] widgets = getWidgets(element);
		fWidgetsToElements.put(item, element);
		if (widgets == null) {
			fElementsToWidgets.put(element, new Widget[] { item });
		} else {
			Widget[] old = widgets;
			Widget[] items = new Widget[old.length + 1];
			System.arraycopy(old, 0, items, 0, old.length);
			items[old.length] = item;
			fElementsToWidgets.put(element, items);
		}
	}

	/**
	 * Removes the update from the pending updates list.
	 * 
	 * @param update
	 */
	protected void updateComplete(IPresentationRequestMonitor update) {
		synchronized (fPendingUpdates) {
			fPendingUpdates.remove(update);
		}
	}

	/**
	 * Unmaps the given item. Does not dispose of the given item,
	 * such that it can be reused.
	 * 
	 * @param kid
	 * @param widget
	 */
	protected synchronized void unmap(Object kid, Widget widget) {
		if (kid == null) {
			// when unmapping a dummy item
			return;
		}
		Widget[] widgets = getWidgets(kid);
		fWidgetsToElements.remove(widget);
		if (widgets != null) {
			for (int i = 0; i < widgets.length; i++) {
				Widget item = widgets[i];
				if (item == widget) {
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
	}

	/**
	 * Returns an image for the given image descriptor or <code>null</code>. Adds the image
	 * to a cache of images if it does not already exist. The cache is cleared when this viewer
	 * is disposed. 
	 * 
	 * @param descriptor image descriptor or <code>null</code>
	 * @return image or <code>null</code>
	 */
	Image getImage(ImageDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}
		Image image = (Image) fImageCache.get(descriptor);
		if (image == null) {
			image = new Image(getControl().getDisplay(), descriptor.getImageData());
			fImageCache.put(descriptor, image);
		}
		return image;
	}

	/**
	 * Returns a font for the given font data or <code>null</code>. Adds the font to this viewer's font 
	 * cache which is disposed when this viewer is disposed.
	 * 
	 * @param fontData font data or <code>null</code>
	 * @return font font or <code>null</code>
	 */
	Font getFont(FontData fontData) {
		if (fontData == null) {
			return null;
		}
		Font font = (Font) fFontCache.get(fontData);
		if (font == null) {
			font = new Font(getControl().getDisplay(), fontData);
			fFontCache.put(fontData, font);
		}
		return font;
	}
	
	/**
	 * Returns a color for the given RGB or <code>null</code>. Adds the color to this viewer's color 
	 * cache which is disposed when this viewer is disposed.
	 * 
	 * @param rgb RGB or <code>null</code>
	 * @return color or <code>null</code>
	 */
	Color getColor(RGB rgb) {
		if (rgb == null) {
			return null;
		}
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
	 */
	public void setContext(IPresentationContext context) {
		fContext = context;
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
		updateLabel(element, item);
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
		updateLabel(element, item);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(org.eclipse.jface.viewers.ISelection, boolean)
	 */
	protected void setSelectionToWidget(ISelection selection, final boolean reveal) {
		if (!acceptsSelection(selection)) {
			selection = getEmptySelection();
		}
		if (fCurrentSelection != null) {
			if (fCurrentSelection.equals(selection) && selection.equals(getSelection())) {
				return;
			}
			fCurrentSelection = null;
		}
		fPendingSelection = selection;
		if (getControl().getDisplay().getThread() == Thread.currentThread()) {
			attemptSelection(reveal);
		} else {
			getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					attemptSelection(reveal);
				}
			});
		}			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	protected void setSelectionToWidget(List l, boolean reveal) {
		// NOT USED
	}	
		
	/**
	 * Attempts to update any pending selection.
	 * 
	 * @param reveal whether to reveal the selection
	 */
	protected synchronized void attemptSelection(boolean reveal) {
		if (fPendingSelection != null) {
			fPendingSelection = doAttemptSelectionToWidget(fPendingSelection, reveal);
			if (fPendingSelection.isEmpty()) {
				fPendingSelection = null;
			}
			ISelection currentSelection = getSelection();
			if (!currentSelection.equals(fCurrentSelection)) {
				fCurrentSelection = currentSelection;
				fireSelectionChanged(new SelectionChangedEvent(this, fCurrentSelection));
			}
		}
	}
	
	/**
	 * Attemtps to selection the specified selection and returns a selection
	 * representing the portion of the selection that could not be honored
	 * and still needs to be selected.
	 * 
	 * @param selection selection to attempt
	 * @param reveal whether to reveal the selection
	 * @return remaining selection
	 */
	protected abstract ISelection doAttemptSelectionToWidget(ISelection selection, boolean reveal);
	
	/**
	 * Returns whether this viewer supports the given selection.
	 * 
	 * @param selection a selection
	 * @return whether this viewer supports the given selection
	 */
	protected abstract boolean acceptsSelection(ISelection selection);
	
	/**
	 * Returns an empty selection supported by this viewer.
	 * 
	 * @return an empty selection supported by this viewer
	 */
	protected abstract ISelection getEmptySelection();
	
	/**
	 * A content provider that does nothing.
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
	 * Notification that a presentation update has failed.
	 * Subclasses may override as required. The default implementation
	 * does nothing.
	 * 
	 * @param monitor monitor for the presentation request that failed
	 * @param status status of update
	 */
	protected void handlePresentationFailure(IPresentationRequestMonitor monitor, IStatus status) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#preservingSelection(java.lang.Runnable)
	 */
	protected synchronized void preservingSelection(Runnable updateCode) {
		// if there's a pending selection, there's no point in preserving the selection
		if (fPendingSelection != null) {
			updateCode.run();
		} else {
			super.preservingSelection(updateCode);
		}
	}
	
	/**
	 * Sets the color attributes of the given widget.
	 * 
	 * @param widget the widget to update
	 * @param foreground foreground color of the widget or <code>null</code> if default
	 * @param background background color of the widget or <code>null</code> if default
	 */
	abstract void setColor(Widget widget, RGB foreground, RGB background);
	
	/**
	 * Sets the label attributes of the given widget.
	 * 
	 * @param widget the widget to update
	 * @param text label text
	 * @param image label image or <code>null</code>
	 */
	void setLabel(Widget widget, String text, ImageDescriptor image) {
		if (widget instanceof Item) {
			Item item = (Item) widget;
			item.setText(text);
			item.setImage(getImage(image));
		}
	}
	
	/**
	 * Sets the font attributes of the given widget.
	 * 
	 * @param widget widget to update
	 * @param font font of the widget or <code>null</code> if default.
	 */
	abstract void setFont(Widget widget, FontData font);
	
	/**
	 * Returns the parent widget of the give widget or <code>null</code>
	 * if none. This method can be called in a non-UI thread.
	 * 
	 * @param widget widget
	 * @return parent widget or <code>null</code>
	 */
	protected abstract Widget getParent(Widget widget);
}
