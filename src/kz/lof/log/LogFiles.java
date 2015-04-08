package kz.lof.log;

import java.io.File;
import java.util.ArrayList;

public class LogFiles {
	public File logDir; 
	
	public LogFiles(){
		logDir = new File("."+File.separator+"logs");
		
	}
	
	public ArrayList<File>  getLogFileList(){
		ArrayList<File> fl = new ArrayList<File>();
		if(logDir.isDirectory()){
			File[] list = logDir.listFiles();
			for(int i = list.length; --i>=0;){
				fl.add(list[i]);
			}
		}
		return fl;
	}
}
