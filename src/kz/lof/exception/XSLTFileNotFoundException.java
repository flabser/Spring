package kz.lof.exception;

public class XSLTFileNotFoundException  extends Exception {
	private static final long serialVersionUID = 179075118057606604L;
	private Exception realException;

	public XSLTFileNotFoundException(String xsltFile) {
		super("xslt файл "+xsltFile+", не найден");
	}
	
	public Exception getRealException() {
		return realException;
	}
}
