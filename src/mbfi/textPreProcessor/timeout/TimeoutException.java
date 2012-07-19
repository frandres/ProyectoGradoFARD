/*
 * Class to handle timeouts when attempting to find matches in the regular expressions
 * 
 * Code belongs to: Óscar Carrascosa Blanco.
 * 
 * http://deckerix.com/blog/como-ejecutar-una-tarea-en-java-con-un-timer-de-ejecucion/
 * 
 * 
 */
package mbfi.textPreProcessor.timeout;
 /** * Signals that the task timed out. */
public class TimeoutException extends Exception {
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Create an instance */
        public TimeoutException() {
        }
    }