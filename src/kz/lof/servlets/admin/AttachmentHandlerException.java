package kz.lof.servlets.admin;

public class AttachmentHandlerException extends Exception {
	private static final long serialVersionUID = -7955384945856281213L;
	private Exception realException;
	
	public AttachmentHandlerException(String error) {
		super(error);			
		realException = this;
	}
	
	public Exception getRealException() {
		return realException;
	}
	
}
