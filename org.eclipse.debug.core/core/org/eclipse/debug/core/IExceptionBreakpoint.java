package org.eclipse.debug.core;

import org.eclipse.core.runtime.CoreException;

public interface IExceptionBreakpoint extends IBreakpoint {
	
	boolean isChecked();
	
	boolean isCaught();
	
	boolean isUncaught();
	
	void setChecked(boolean checked) throws CoreException;
	
	void setCaught(boolean caught) throws CoreException;
	
	void setUncaught(boolean uncaught) throws CoreException;

}

