package org.eclipse.debug.internal.core;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.IBreakpoint;
import org.eclipse.debug.core.IDebugConstants;
import org.eclipse.debug.core.model.IDebugTarget;

public abstract class Breakpoint implements IBreakpoint {
	
	/**
	 * The set of attributes used to configure a breakpoint
	 */
	protected static final String[] fgBreakpointAttributes= new String[]{IDebugConstants.MODEL_IDENTIFIER, IDebugConstants.ENABLED};	
	
	protected IMarker fMarker= null;
	protected boolean installed= false;

	/**
	 * Constructor for Breakpoint
	 */
	public Breakpoint() {
	}
	
	/**
	 * Create a breakpoint for the given marker
	 */
	public Breakpoint(IMarker marker) {
		fMarker= marker;
	}
	
	/**
	 * Returns whether the given object is equal to this object.
	 * 
	 * Two breakpoints are equal if their markers have the same id.
	 * A breakpoint is not equal to any other kind of object.
	 */
	public boolean equals(Object item) {
		if (item instanceof IBreakpoint) {
			return getId() == ((IBreakpoint)item).getId();
		}
		return false;
	}
	
	/**
	 * @see IBreakpoint
	 */
	public void configure(String modelIdentifier, boolean enabled) throws CoreException {
		setAttributes(fgBreakpointAttributes, new Object[]{modelIdentifier, new Boolean(enabled)});
	}	
	
	/**
	 * @see IBreakpoint#addToTarget(IDebugTarget)
	 */
	public abstract void addToTarget(IDebugTarget target);
	
	/**
	 * @see IBreakpoint#changeForTarget(IDebugTarget)
	 */
	public abstract void changeForTarget(IDebugTarget target);
	
	/**
	 * @see IBreakpoint#removeFromTarget(IDebugTarget)
	 */
	public abstract void removeFromTarget(IDebugTarget target);
	
	/**
	 * Enable the breakpoint
	 */
	public void enable() throws CoreException {
		fMarker.setAttribute(IDebugConstants.ENABLED, true);
	}
	
	/**
	 * Returns whether the breakpoint is enabled
	 */
	public boolean isEnabled() throws CoreException {
		return fMarker.getAttribute(IDebugConstants.ENABLED, false);
	}
	
	/**
	 * @see IBreakpoint#toggleEnabled()
	 */
	public void toggleEnabled() throws CoreException {
		if (isEnabled()) {
			disable();
		} else {
			enable();
		}
	}
	
	/**
	 * Disable the breakpoint
	 */
	public void disable() throws CoreException {
		fMarker.setAttribute(IDebugConstants.ENABLED, false);		
	}
	
	/**
	 * Returns whether the breakpoint is disabled
	 */
	public boolean isDisabled() throws CoreException {
		return !isEnabled();
	}

	/**
	 * @see IBreakpoint#delete()
	 */
	public void delete() throws CoreException {
		fMarker.delete();
	}

	/**
	 * @see IBreakpoint#exists()
	 */
	public boolean exists() {
		return fMarker.exists();
	}
	
	/**
	 * @see IBreakpoint#getMarker()
	 */
	public IMarker getMarker() {
		return fMarker;
	}

	/**
	 * @see IBreakpoint#getAttribute(String)
	 */
	public Object getAttribute(String attributeName) throws CoreException {
		return fMarker.getAttribute(attributeName);
	}

	/**
	 * @see IBreakpoint#getAttribute(String, int)
	 */
	public int getAttribute(String attributeName, int defaultValue) {
		return fMarker.getAttribute(attributeName, defaultValue);
	}

	/**
	 * @see IBreakpoint#getAttribute(String, String)
	 */
	public String getAttribute(String attributeName, String defaultValue) {
		return fMarker.getAttribute(attributeName, defaultValue);
	}

	/**
	 * @see IBreakpoint#getAttribute(String, boolean)
	 */
	public boolean getAttribute(String attributeName, boolean defaultValue) {
		return fMarker.getAttribute(attributeName, defaultValue);
	}

	/**
	 * @see IBreakpoint#getAttributes()
	 */
	public Map getAttributes() throws CoreException {
		return fMarker.getAttributes();
	}

	/**
	 * @see IBreakpoint#getAttributes(String[])
	 */
	public Object[] getAttributes(String[] attributeNames) throws CoreException {
		return fMarker.getAttributes(attributeNames);
	}
	
	/**
	 * Sets the <code>boolean</code> attribute of the given breakpoint.
	 */
	protected void setBooleanAttribute(String attribute, boolean value) throws CoreException {
		setAttribute(attribute, value);	
	}

	/**
	 * Returns the <code>boolean</code> attribute of the given breakpoint
	 * or <code>false</code> if the attribute is not set.
	 */
	protected boolean getBooleanAttribute(String attribute) {
		return getAttribute(attribute, false);
	}	

	/**
	 * @see IBreakpoint#getId()
	 */
	public long getId() {
		return fMarker.getId();
	}

	/**
	 * @see IBreakpoint#getResource()
	 */
	public IResource getResource() {
		return fMarker.getResource();
	}	

	/**
	 * @see IBreakpoint#getType()
	 */
	public String getType() throws CoreException {
		return fMarker.getType();
	}

	/**
	 * @see IBreakpointManager
	 */
	public int getLineNumber() {
		return getAttribute(IMarker.LINE_NUMBER, -1);
	}

	/**
	 * @see IBreakpointManager
	 */
	public int getCharStart() {
		return getAttribute(IMarker.CHAR_START, -1);
	}

	/**
	 * @see IBreakpointManager
	 */
	public int getCharEnd() {
		return getAttribute(IMarker.CHAR_END, -1);
	}

	/**
	 * Returns the model identifier for the given breakpoint.
	 */
	public String getModelIdentifier() {
		return (String)getAttribute(IDebugConstants.MODEL_IDENTIFIER, null);
	}	

	/**
	 * @see IBreakpoint#isSubtypeOf(String)
	 */
	public boolean isSubtypeOf(String superType) throws CoreException {
		return fMarker.isSubtypeOf(superType);
	}

	/**
	 * @see IBreakpoint#setAttribute(String, int)
	 */
	public void setAttribute(String attributeName, int value)
		throws CoreException {
			fMarker.setAttribute(attributeName, value);
	}

	/**
	 * @see IBreakpoint#setAttribute(String, Object)
	 */
	public void setAttribute(String attributeName, Object value)
		throws CoreException {
			fMarker.setAttribute(attributeName, value);
	}

	/**
	 * @see IBreakpoint#setAttribute(String, boolean)
	 */
	public void setAttribute(String attributeName, boolean value)
		throws CoreException {
			fMarker.setAttribute(attributeName, value);
	}

	/**
	 * @see IBreakpoint#setAttributes(String[], Object[])
	 */
	public void setAttributes(String[] attributeNames, Object[] values)
		throws CoreException {
			fMarker.setAttributes(attributeNames, values);
	}

	/**
	 * @see IBreakpoint#setAttributes(Map)
	 */
	public void setAttributes(Map attributes) throws CoreException {
		fMarker.setAttributes(attributes);
	}

}

