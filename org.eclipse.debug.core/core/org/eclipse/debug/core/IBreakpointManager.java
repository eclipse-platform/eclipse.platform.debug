package org.eclipse.debug.core;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.core.Breakpoint;

/**
 * The breakpoint manager manages the collection of breakpoints
 * in the workspace. A breakpoint suspends the execution of a
 * program being debugged. The kinds of breakpoint supported by each
 * debug architecture and the information required to create those
 * breakpoints is dictated by each debug architecture.
 * <p>
 * Breakpoints are implemented by markers. The debug plug-in defines a root
 * breakpoint marker of which all breakpoints should be subtypes. The
 * debug plug-in also defines a common line breakpoint. Convenience methods
 * are defined to configure the attributes of breakpoints defined by
 * the debug plug-in. See <code>configureBreakpoint(IMarker, String, boolean)</code>
 * and <code>configureLineBreakpoint(IMarker, String, boolean, int, int, int)</code>.
 * </p>
 * <p>All breakpoints have:<ul>
 * <li> a model identifier, specifying the identifier of the debug model the breakpoint
 *      is intended for</li>
 * <li> a enabled attribute, specifying if the breakpoint is enabled or disabled</li>.
 * </ul>
 * Additionally, all line breakpoints have a line number and/or a character start and
 * end range.
 * </p>
 * <p>
 * Breakpoint creation is a client responsibility. Creation of a breakpoint requires
 * the creation of a marker. It is a client responsibility to determine which 
 * resource a breakpoint marker should be associated/persisted with. Breakpoints
 * are only considered active when registered with the breakpoint manager. 
 * </p>
 * <p>
 * As launches are registered and deregistered, the breakpoint
 * manager automatically adds and removes debug targets associated with those
 * lauches, as breakpoint listeners.
 * Debug targets (implementors of <code>IBreakpointSupport</code>) are
 * informed of breakpoint additions, removals, and changes if they
 * respond <code>true</code> to <code>supportsBreakpoints()</code>.
 * A debug target is responsible for installing all existing
 * (applicable) breakpoints when created.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * @see IMarker
 * @see org.eclipse.debug.core.model.IBreakpointSupport
 */
public interface IBreakpointManager {

	/**
	 * Adds the given breakpoint to the collection of active breakpoints
	 * in the workspace and notifies all registered listeners. This has no effect
	 * if the given breakpoint is already registered.
	 *
	 * @param breakpoint the breakpoint to add
	 *
	 * @exception DebugException if adding fails. Reasons include:<ul>
	 * <li>CONFIGURATION_INVALID - the required <code>MODEL_IDENTIFIER</code> attribute
	 * 	is not set on the breakpoint marker.</li>
	 * <li>A <code>CoreException</code> occurred while verifying the <code>MODEL_IDENTIFIER</code>
	 *	attribute.</li>
	 * </ul>
	 */
	void addBreakpoint(IBreakpoint breakpoint) throws DebugException;

	/**
	 * Create a breakpoint for the given marker and add it.
	 * 
	 * @return the breakpoint that is created
	 */
	IBreakpoint loadMarker(IMarker marker) throws DebugException;
	
	/**
	 * Returns a collection of all existing breakpoints.
	 * Returns an empty array if no breakpoints exist.
	 *
	 * @return an array of breakpoint markers
	 */
	IBreakpoint[] getBreakpoints();
	
	/**
	 * Returns a collection of all existing markers.
	 * Returns an empty array if no markers exist.
	 *
	 * @return an array of breakpoint markers
	 */
	IMarker[] getMarkers();	
	
	/**
	 * Returns a collection of all breakpoints registered for the
	 * given debug model. Answers an empty array if no breakpoints are registered
	 * for the given debug model.
	 *
	 * @param modelIdentifier identifier of a debug model plug-in
	 * @return an array of breakpoint markers
	 */
	IBreakpoint[] getBreakpoints(String modelIdentifier);
	
	/**
	 * Returns a collection of all markers registered for the
	 * given debug model. Answers an empty array if no markers are registered
	 * for the given debug model.
	 *
	 * @param modelIdentifier identifier of a debug model plug-in
	 * @return an array of breakpoint markers
	 */
	IMarker[] getMarkers(String modelIdentifier);	
	
	/**
	 * Returns the breakpoint that is associated with marker or
	 * <code>null</code> if no such breakpoint exists
	 * 
	 * @param marker the marker
	 * @return the breakpoint associated with the marker or null if none exists
	 */
	IBreakpoint getBreakpoint(IMarker marker);
	
	/**
	 * Sets the value of the <code>ENABLED</code> attribute of the
	 * given breakpoint. This is a convenience method for
	 * <code>IMarker.setAttribute(String, boolean)</code>.
	 *
	 * @exception CoreException if setting the attribute fails
	 * @see IMarker#setAttribute(String, boolean)
	 */
	void setEnabled(IMarker breakpoint, boolean value) throws CoreException;
	
	/**
	 * Returns whether the given breakpoint is currently
	 * registered with this breakpoint manager.
	 *
	 * @return whether the breakpoint is registered
	 */
	boolean isRegistered(IBreakpoint breakpoint);	
	
	/**
	 * Returns whether the given marker is currently
	 * registered with this breakpoint manager.
	 *
	 * @return whether the marker is registered
	 */
	boolean isRegistered(IMarker marker);		
	
	/**
	 * Returns the value of the line number attribute of the
	 * given breakpoint or -1 if the attribute is not present or
	 * an exception occurs while accessing the attribute.
	 * 
	 * @param breakpoint the breakpoint
	 * @return the breakpoint's line number, or -1 if unknown
	 */
	int getLineNumber(IMarker marker);
	
	/**
	 * Returns the value of the char start attribute of the
	 * given breakpoint or -1 if the attribute is not present, or
	 * an exception occurs while accessing the attribute.
	 * 
	 * @param breakpoint the breakpoint
	 * @return the breakpoint's char start value, or -1 if unknown
	 */
	int getCharStart(IMarker marker);
	
	/**
	 * Returns the value of the <code>CHAR_END</code> attribute of the
	 * given breakpoint or -1 if the attribute is not present or
	 * an exception occurs while accessing the attribute.
	 * This is a convenience method for <code>IMarker.getAttribute(String, int)</code>.
	 *
	 * @param breakpoint the breakpoint
	 * @return the breakpoint's char end value, or -1 if unknown
	 */
	int getCharEnd(IMarker marker);
	
	/**
	 * Returns the value of the <code>MODEL_IDENTIFIER</code> attribute of the
	 * given breakpoint or <code>null</code> if the attribute is not present or
	 * an exception occurs while accessing the attribute. This is a convenience method
	 * for <code>IMarker.getAttribute(String, String)</code>.
	 *
	 * @param breakpoint the breakpoint
	 * @return the breakpoint's debug model plug-in identifier, or <code>null</code>
	 *    if an exception occurs retrieving the attribute
	 */
	String getModelIdentifier(IMarker breakpoint);

	/**
	 * Removes the given breakpoint from the breakpoint manager, and notifies all
	 * registered listeners. The marker is deleted if the <code>delete</code> flag is
	 * true. Has no effect if the given breakpoint is not currently registered.
	 *
	 * @param breakpoint the breakpoint to remove
	 * @param delete whether the breakpoint marker should be deleted
	 * @exception CoreException if an exception occurs while deleting the marker.
	 */
	void removeBreakpoint(IBreakpoint breakpoint, boolean delete) throws CoreException;

	/**
	 * Removes the breakpoint associated with the given marker from the breakpoint manager, and notifies all
	 * registered listeners. The marker is deleted if the <code>delete</code> flag is
	 * true. Has no effect if the given breakpoint is not currently registered.
	 *
	 * @param marker the marker to remove
	 * @param delete whether the breakpoint marker should be deleted
	 * @exception CoreException if an exception occurs while deleting the marker.
	 */
	void removeBreakpoint(IMarker marker, boolean delete) throws CoreException;

	/**
	 * Adds the given listener to the collection of registered breakpoint listeners.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener the listener to add
	 */
	void addBreakpointListener(IBreakpointListener listener);

	/**
	 * Removes the given listener from the collection of registered breakpoint listeners.
	 * Has no effect if an identical listener is not already registered.
	 *
	 * @param listener the listener to remove	
	 */
	void removeBreakpointListener(IBreakpointListener listener);
	
}


