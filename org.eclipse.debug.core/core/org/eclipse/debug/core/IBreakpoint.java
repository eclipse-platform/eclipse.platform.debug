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
 * Configures the given breakpoint's <code>MODEL_IDENTIFIER</code>
 * and <code>ENABLED</code> attributes to the given values.
 * This is a convenience method for
 * <code>IMarker.setAttribute(String, Object)</code> and
 * <code>IMarker.setAttribute(String, boolean)</code>.
 * <code>IMarker.setAttribute(String, int)</code>.
 *
 * @param breakpoint the breakpoint marker to configure
 * @param modelIdentifier the identifier of the debug model plug-in
 *    the breakpoint is associated with
 * @param enabled the initial value of the enabled attribute of the
 *	breakpoint marker
 * 
 * @exception CoreException if setting an attribute fails
 * @see IMarker#setAttribute(String, Object)
 * @see IMarker#setAttribute(String, boolean)
 * @see IMarker#setAttribute(String, int)
 */
public void configure(String modelIdentifier, boolean enabled) throws CoreException;
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
 * Returns the attribute with the given name.  The result is an instance of one
 * of the following classes: <code>String</code>, <code>Integer</code>, 
 * or <code>Boolean</code>.
 * Returns <code>null</code> if the attribute is undefined.
 *
 * @param attributeName the name of the attribute
 * @return the value, or <code>null</code> if the attribute is undefined.
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public Object getAttribute(String attributeName) throws CoreException;
/**
 * Returns the integer-valued attribute with the given name.  
 * Returns the given default value if the attribute is undefined.
 * or the marker does not exist or is not an integer value.
 *
 * @param attributeName the name of the attribute
 * @param defaultValue the value to use if no value is found
 * @return the value or the default value if no value was found.
 */
public int getAttribute(String attributeName, int defaultValue);
/**
 * Returns the string-valued attribute with the given name.  
 * Returns the given default value if the attribute is undefined
 * or the marker does not exist or is not a string value.
 *
 * @param attributeName the name of the attribute
 * @param defaultValue the value to use if no value is found
 * @return the value or the default value if no value was found.
 */
public String getAttribute(String attributeName, String defaultValue);
/**
 * Returns the boolean-valued attribute with the given name.  
 * Returns the given default value if the attribute is undefined
 * or the marker does not exist or is not a boolean value.
 *
 * @param attributeName the name of the attribute
 * @param defaultValue the value to use if no value is found
 * @return the value or the default value if no value was found.
 */
public boolean getAttribute(String attributeName, boolean defaultValue);
/**
 * Returns a map with all the attributes for the marker.
 * If the marker has no attributes then <code>null</code> is returned.
 *
 * @return a map of attribute keys and values (key type : <code>String</code> 
 *		value type : <code>String</code>, <code>Integer</code>, or 
 *		<code>Boolean</code>) or <code>null</code>.
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public Map getAttributes() throws CoreException;
/**
 * Returns the attributes with the given names.  The result is an an array 
 * whose elements correspond to the elements of the given attribute name
 * array.  Each element is <code>null</code> or an instance of one
 * of the following classes: <code>String</code>, <code>Integer</code>, 
 * or <code>Boolean</code>.
 *
 * @param attributeNames the names of the attributes
 * @return the values of the given attributes.
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public Object[] getAttributes(String[] attributeNames) throws CoreException;
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
 * Returns the type of this marker.
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
 * Returns whether the type of this marker is considered to be a subtype of
 * the given marker type. 
 *
 * @return boolean <code>true</code>if the marker's type
 *		is the same as (or a subtype of) the given type.
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public boolean isSubtypeOf(String superType) throws CoreException;
/**
 * Sets the integer-valued attribute with the given name.  
 * <p>
 * This method changes resources; these changes will be reported
 * in a subsequent resource change event, including an indication 
 * that this marker has been modified.
 * </p>
 *
 * @param attributeName the name of the attribute
 * @param value the value
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public void setAttribute(String attributeName, int value) throws CoreException;
/**
 * Sets the attribute with the given name.  The value must be <code>null</code> or 
 * an instance of one of the following classes: 
 * <code>String</code>, <code>Integer</code>, or <code>Boolean</code>.
 * If the value is <code>null</code>, the attribute is considered to be undefined.
 * <p>
 * This method changes resources; these changes will be reported
 * in a subsequent resource change event, including an indication 
 * that this marker has been modified.
 * </p>
 *
 * @param attributeName the name of the attribute
 * @param value the value, or <code>null</code> if the attribute is to be undefined
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public void setAttribute(String attributeName, Object value) throws CoreException;
/**
 * Sets the boolean-valued attribute with the given name.  
 * <p>
 * This method changes resources; these changes will be reported
 * in a subsequent resource change event, including an indication 
 * that this marker has been modified.
 * </p>
 *
 * @param attributeName the name of the attribute
 * @param value the value
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public void setAttribute(String attributeName, boolean value) throws CoreException;
/**
 * Sets the given attribute key-value pairs on this marker.
 * The values must be <code>null</code> or an instance of 
 * one of the following classes: <code>String</code>, 
 * <code>Integer</code>, or <code>Boolean</code>.
 * If a value is <code>null</code>, the new value of the 
 * attribute is considered to be undefined.
 * <p>
 * This method changes resources; these changes will be reported
 * in a subsequent resource change event, including an indication 
 * that this marker has been modified.
 * </p>
 *
 * @param attributeNames an array of attribute names
 * @param values an array of attribute values
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public void setAttributes(String[] attributeNames, Object[] values) throws CoreException;
/**
 * Sets the attributes for this marker to be the ones contained in the
 * given table. The values must be an instance of one of the following classes: 
 * <code>String</code>, <code>Integer</code>, or <code>Boolean</code>.
 * Attributes previously set on the marker but not included in the given map
 * are considered to be removals. Setting the given map to be <code>null</code>
 * is equivalent to removing all marker attributes.
 * <p>
 * This method changes resources; these changes will be reported
 * in a subsequent resource change event, including an indication 
 * that this marker has been modified.
 * </p>
 *
 * @param attributes a map of attribute names to attribute values 
 *		(key type : <code>String</code> value type : <code>String</code>, 
 *		<code>Integer</code>, or <code>Boolean</code>) or <code>null</code>
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This marker does not exist.</li>
 * </ul>
 */
public void setAttributes(Map attributes) throws CoreException;
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

