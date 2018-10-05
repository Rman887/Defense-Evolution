package com.rman.engine.gui;

/**
 * <pre>public class WindowException extends Exception</pre>
 * 
 * <p> An exception caused by a {@link Window Window}. </p>
 * 
 * @author Arman
 * 
 * @see java.lang.Exception
 */
public class WindowException extends Exception {

	private static final long serialVersionUID = -473980456101036647L;

	/**
	 * <pre>public WindowException()</pre>
	 * 
	 * <p>Creates a new <code>WindowException</code>.</p>
	 * 
	 * @see java.lang.Exception
	 */
	public WindowException() {
		super();
	}

	/**
	 * <pre>public WindowException({@link String String} message)</pre>
	 * 
	 * <p>Creates a new <code>WindowException</code> with the given message.<p>
	 * 
	 * @param message - The message of the exception
	 * 
	 * @see java.lang.Exception
	 */
	public WindowException(String message) {
		super(message);
	}

	/**
	 * <pre>public WindowException({@link Throwable Throwable} cause)</pre>
	 * 
	 * <p>Creates a new <code>WindowException</code> with the given cause.</p>
	 * 
	 * @param cause - The <code>Throwable</code> that caused this exception
	 * 
	 * @see java.lang.Exception
	 */
	public WindowException(Throwable cause) {
		super(cause);
	}

	/**
	 * <pre>public WindowException({@link String String} message, {@link Throwable Throwable} cause)</pre>
	 * 
	 * <p>Creates a new <code>WindowException</code> with the given message and cause.</p>
	 * 
	 * @param message - The message of the exception
	 * @param cause - The <code>Throwable</code> that caused this exception
	 * 
	 * @see java.lang.Exception
	 */
	public WindowException(String message, Throwable cause) {
		super(message, cause);
	}
}
