package org.eclipse.debug.core;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugTarget;

public interface IBreakpoint {
	/*====================================================================
	 * Marker types:
	 *====================================================================*/
	
	/** 
	 * Base marker type 
	 *
	 * @see #getType
	 */
	public static final String MARKER = ResourcesPlugin.PI_RESOURCES + ".marker";

	/** 
	 * Task marker type 
	 *
	 * @see #getType
	 */
	public static final String TASK = ResourcesPlugin.PI_RESOURCES + ".taskmarker";

	/** 
	 * Problem marker type 
	 *
	 * @see #getType
	 */
	public static final String PROBLEM = ResourcesPlugin.PI_RESOURCES + ".problemmarker";

	/** 
	 * Text marker type 
	 *
	 * @see #getType
	 */
	public static final String TEXT = ResourcesPlugin.PI_RESOURCES + ".textmarker";

	/** 
	 * Bookmark marker type 
	 *
	 * @see #getType
	 */
	public static final String BOOKMARK = ResourcesPlugin.PI_RESOURCES + ".bookmark";

	/*====================================================================
	 * Marker attributes:
	 *====================================================================*/
	
	/** 
	 * Severity marker attribute.  A number from the set of error, warning and info
	 * severities defined by the plaform.
	 *
	 * @see #SEVERITY_ERROR
	 * @see #SEVERITY_WARNING
	 * @see #SEVERITY_INFO
	 * @see #getAttribute
	 */
	public static final String SEVERITY = "severity";
	
	/** 
	 * Message marker attribute.  A localized string describing the nature
	 * of the marker (e.g., a name for a bookmark or task).  The content
	 * and form of this attribute is not specified or interpreted by the platform.
	 *
	 * @see #getAttribute
	 */
	public static final String MESSAGE = "message";
	
	/** 
	 * Location marker attribute.  The location is a human-readable (localized) string which
	 * can be used to distinguish between markers on a resource.  As such it 
	 * should be concise and aimed at users.  The content and 
	 * form of this attribute is not specified or interpreted by the platform.
	 *
	 * @see #getAttribute
	 */
	public static final String LOCATION = "location";
	
	/** 
	 * Priority marker attribute.  A number from the set of high, normal and low 
	 * priorities defined by the plaform.
	 * 
	 * @see #PRIORITY_HIGH
	 * @see #PRIORITY_NORMAL
	 * @see #PRIORITY_LOW
	 * @see #getAttribute
	 */
	public static final String PRIORITY = "priority";
	
	/** 
	 * Done marker attribute.  A boolean value indicating whether 
	 * the marker (e.g., a task) is considered done.  
	 *
	 * @see #getAttribute
	 */
	public static final String DONE = "done";

	/** 
	 * Character start marker attribute.  An integer value indicating where a text
	 * marker starts.  This attribute is zero-relative and inclusive.
	 *
	 * @see #getAttribute
	 */
	public static final String CHAR_START = "charStart";

	/** 
	 * Character end marker attribute.  An integer value indicating where a text
	 * marker ends.  This attribute is zero-relative and exclusive.
	 *
	 * @see #getAttribute
	 */
	public static final String CHAR_END = "charEnd";

	/** 
	 * Line number marker attribute.  An integer value indicating the line number
	 * for a text marker.  This attribute is 1-relative.
	 *
	 * @see #getAttribute
	 */
	public static final String LINE_NUMBER = "lineNumber";

	/*====================================================================
	 * Marker attributes values:
	 *====================================================================*/
	
	/** 
	 * High priority constant (value 2).
	 *
	 * @see #getAttribute
	 */
	public static final int PRIORITY_HIGH = 2;
	
	/** 
	 * Normal priority constant (value 1).
	 *
	 * @see #getAttribute
	 */
	public static final int PRIORITY_NORMAL = 1;
	
	/** 
	 * Low priority constant (value 0).
	 *
	 * @see #getAttribute
	 */
	public static final int PRIORITY_LOW = 0;

	/** 
	 * Error severity constant (value 2) indicating an error state.
	 *
	 * @see #getAttribute
	 */
	public static final int SEVERITY_ERROR= 2;
	
	/** 
	 * Warning severity constant (value 1) indicating a warning.
	 *
	 * @see #getAttribute
	 */
	public static final int SEVERITY_WARNING = 1;
	
	/** 
	 * Info severity constant (value 0) indicating information only.
	 *
	 * @see #getAttribute
	 */
	public static final int SEVERITY_INFO = 0;
	

/**
 * Deletes this marker from its associated resource.  This method has no
 * effect if this marker does not exist.
 *
 * @exception CoreException if this marker could not be deleted. Reasons include:
 * <ul>
 * <li> Resource changes are disallowed during resource change event notification.</li>
 * </ul>
 */
public void delete() throws CoreException;
/**
 * Tests this marker for equality with the given object.
 * Two markers are equal iff they have the same id.
 * Markers are assigned an id when created on a resource.
 *
 * @param object the other object
 * @return an indication of whether the objects are equal
 */
public boolean equals(Object object);
/**
 * Returns whether this marker exists in the workspace.  A marker
 * exists if its resource exists and has a marker with the marker's id.
 *
 * @return <code>true</code> if this marker exists, otherwise
 *    <code>false</code>
 */
public boolean exists();
/**
 * Enable this breakpoint
 */
public void enable() throws CoreException;
/**
 * Disable this breakpoint
 */
public void disable() throws CoreException;
/**
 * Returns the marker associated with the breakpoint.
 * 
 * @return the marker, or <code>null</code> if the marker does not exist.
 */
public IMarker getMarker();
/**
 * Returns the model identifier for this breakpoint.
 */
public String getModelIdentifier();

/**
 * Returns the value of the <code>LINE_NUMBER</code> attribute of the
 * given breakpoint or -1 if the attribute is not present or
 * an exception occurs while accessing the attribute. This is a
 * convenience method for <code>IMarker.getAttribute(String, int)</code>.
 *
 * @param breakpoint the breakpoint
 * @return the breakpoint's line number, or -1 if unknown
 */
public int getLineNumber();
/**
 * Returns the value of the <code>CHAR_START</code> attribute of the
 * given breakpoint or -1 if the attribute is not present, or
 * an exception occurs while accessing the attribute. This is a
 * convenience method for <code>IMarker.getAttribute(String, int)</code>
 * 
 * @param breakpoint the breakpoint
 * @return the breakpoint's char start value, or -1 if unknown
 */
public int getCharStart();
/**
 * Returns the value of the <code>CHAR_END</code> attribute of the
 * given breakpoint or -1 if the attribute is not present or
 * an exception occurs while accessing the attribute.
 * This is a convenience method for <code>IMarker.getAttribute(String, int)</code>.
 *
 * @param breakpoint the breakpoint
 * @return the breakpoint's char end value, or -1 if unknown
 */
public int getCharEnd();

/**
 * Returns the id of the marker.  The id of a marker is unique
 * relative to the resource with which the marker is associated.
 * Marker ids are not globally unique.
 *
 * @return the id of the marker
 * @see IResource#findMarker
 */
public long getId();
/**
 * Returns the resource with which this marker is associated. 
 *
 * @return the resource with which this marker is associated
 */
public IResource getResource();
/**
 * Returns the type of this breakpoint.
 *
 * @return the type of this marker
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public String getType() throws CoreException;
/**
 * Returns whether this breakpoint is enabled
 */
public boolean isEnabled() throws CoreException;
/**
 * Returns whether this breakpoint is disabled
 */
public boolean isDisabled() throws CoreException;
/**
 * Sets the enabled state of this breakpoint to the opposite of its
 * current state.
 */
public void toggleEnabled() throws CoreException;

/**
 * Install a breakpoint request for this breakpoint in the given target.
 * 
 * @param target the debug target into which the request should be added.
 */
public abstract void addToTarget(IDebugTarget target);
/**
 * Update the breakpoint request for this breakpoint in the given target.
 * 
 * @param target the debug target for which the request should be updated.
 */
public abstract void changeForTarget(IDebugTarget target);
/**
 * Remove the breakpoint request for this breakpoint from the given target.
 * 
 * @param target the debug target from which the request should be removed.
 */
public abstract void removeFromTarget(IDebugTarget target);

}

