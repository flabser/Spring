package kz.lof.util;

import java.util.*;
import kz.lof.exception.PortalException;
import kz.lof.servlets.SignalType;

public class XMLResponse {
	public ResponseType type;
	public boolean resultFlag;

	protected static final String xmlTextUTF8Header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	
	private String responseStatus = "undefined";
	private String formSesID = "";
	private String redirectURL = "";
	private ArrayList<Message> messages = new ArrayList<Message>();		
	private ArrayList<Signal> signals = new ArrayList<Signal>();		
	private int messageCount = 1;
	
	public XMLResponse(ResponseType type) {		
		this.type = type;	
	}
	
	public XMLResponse(ResponseType type, boolean b) {
		this.type = type;	
		setResponseStatus(b);
	}
	
	public XMLResponse(Exception e) {
		resultFlag = false;
		responseStatus = "error";		
		addMessage(e.getMessage());
		addMessage(PortalException.errorMessage(e));
	}
	
	public void setResponseType(ResponseType type) {
		this.type = type;
	}
	
	public void setRedirect(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
	public void setResponseStatus(boolean responseStatus) {
		resultFlag = responseStatus;
		if (responseStatus){
			this.responseStatus = "ok";
		}else{
			this.responseStatus = "error";
		}		
	}
	
	public void setFormSesID(String fsid){
		formSesID = fsid;
	}
	
	public void addMessage(String message) {
		messages.add(new Message(message, Integer.toString(++ messageCount)));
	}
	
	public void setMessage(String message) {
		messages.clear();		
		messages.add(new Message(message, Integer.toString(messageCount)));
	}
	
	public void addMessage(String message, String id){
		if (id != null){
			messages.add(new Message(message, id));
		}else{
			addMessage(message);
		}
	}
	
	public void setMessage(String message, String id) {
		messages.clear();	
		if (id != null){
			messages.add(new Message(message, id));
		}else{
			setMessage(message);
		}
	}
		
	public void addSignal(SignalType signal) {
		signals.add(new Signal(signal));
	}
	
	
	public void addReloadSignal() {
		signals.add(new Signal(SignalType.RELOAD_PAGE));
	}
	
	public String toXML() {
		StringBuffer result = new StringBuffer(100);		
		result.append("<response type=\"" + type + "\" status=\"" + responseStatus + "\">");	
		for (Signal sig: signals) {			
			result.append(sig.toXML()); 
		}
		for (Message msg: messages) {			
			result.append(msg.toXML()); 
		}
		result.append("<redirect>" + redirectURL + "</redirect>");
		result.append("</response>");
		return result.toString();
	}
	
	public String toCompleteXML() {		
		return xmlTextUTF8Header + toXML();
	}
	
	class Message{
		String text;
		String id;
		
		Message(String text, String id){
			this.text = text;
			this.id = id;
		}
		
		String toXML(){
			return "<message id=\"" + id + "\" formsesid=\"" + formSesID + "\">" + text + "</message>";
		}
		
	}
	
	class Signal{
		SignalType signal;
	
		Signal(SignalType signal){
			this.signal = signal;			
		}
		
		String toXML(){
			return "<signal>" + signal.toString() + "</signal>";
		}
		
	}
	
}
