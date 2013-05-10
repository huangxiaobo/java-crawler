package com.routon.jui;

public class JuiException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 911L;

	public JuiException() {
	
	}

	public JuiException(String name) {
		super(name);
	}

	public JuiException(String name, Throwable cause) {
		super(name, cause);
	}

	public JuiException(Exception cause) {
		super(cause);
	}
}
