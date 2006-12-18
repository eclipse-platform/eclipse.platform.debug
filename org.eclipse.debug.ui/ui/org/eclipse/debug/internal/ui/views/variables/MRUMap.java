/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.debug.internal.ui.views.variables;

import java.util.LinkedHashMap;

import org.eclipse.jface.text.Assert;

/**
 * Most recently used variant with capped size that only counts
 * {@linkplain #put(Object, Object) put} as access. This is implemented by always removing an
 * element before it gets put back.
 * 
 * @since 3.2.2
 */
public final class MRUMap extends LinkedHashMap {
	private static final long serialVersionUID= 1L;
	private final int fMaxSize;
	
	/**
	 * Creates a new <code>MRUMap</code> with the given size.
	 * 
	 * @param maxSize the maximum size of the cache, must be &gt; 0
	 */
	public MRUMap(int maxSize) {
		Assert.isLegal(maxSize > 0);
		fMaxSize= maxSize;
	}
	
	/*
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		Object object= remove(key);
		super.put(key, value);
		return object;
	}
	
	/*
	 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
	 */
	protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
		return size() > fMaxSize;
	}
}
