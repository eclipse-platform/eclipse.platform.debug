package org.eclipse.debug.core;

import org.eclipse.core.resources.IMarker;

/**
 * Creates breakpoints from markers
 */
public interface IBreakpointFactory {

	/**
	 * Create a breakpoint for the given marker based on the marker type
	 */
	IBreakpoint createBreakpointFor(IMarker marker) throws DebugException;

}

