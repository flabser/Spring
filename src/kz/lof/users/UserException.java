package kz.lof.users;

import kz.lof.exception.ExceptionType;

public class UserException extends Exception {
	public int id;
	public String user;
	public ExceptionType exceptionType;
	
	private static final long serialVersionUID = 4762010135613823296L;
	private String errorText;
	
		
	public UserException(UserExceptionType error, String user) {
		super();
		this.user = user;
		switch(error){ 
		case REDIRECT_URL_NOT_DEFINED:		
		
			break;		
		}		
	}
	
	public String getMessage(){
		return errorText;
	}
	
	public String toString(){
		return errorText;
	}

}
