package kz.lof.dataengine;

public class DatabasePoolException  extends Exception {
	public DatabasePoolExceptionType id;
	public String user;

	private static final long serialVersionUID = 6120594147766886379L;
	private String errorText;
	
	public DatabasePoolException(DatabasePoolExceptionType error, String dbID) {
		super();
		id = error;
		switch(id){
		case DATABASE_CONNECTION_REFUSED:
			errorText = "Database connection refused (" + dbID + ")";
			break;
		case DATABASE_AUTHETICATION_FAILED:
			errorText = "password authentication failed (" + dbID + ")";
			break;
		case DATABASE_SQL_ERROR:
			errorText = "Database SQL error (" + dbID + ")";
			break;
		case DATABASE_CONNECTION_ATTEMPT_FAILED:
			errorText = "Database connection attempt failed (" + dbID + ")";
			break;
		default:
			errorText = "Internal database eror (" + dbID + ")";
		
		}		
	}
	
	public String getMessage(){
		return errorText;
	}
	
	public String toString(){
		return errorText;
	}
}
