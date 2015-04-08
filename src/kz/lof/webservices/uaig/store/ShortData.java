package kz.lof.webservices.uaig.store;

import java.util.Date;


public class ShortData {
    public ShortData(){}
    public ShortData(int idKarta, String incomingNumber, String docType,
            String corrAddr, String rnn, Date date){
        this.idKarta = idKarta;
        this.incomingNumber = incomingNumber;
        this.docType = docType;
        this.corrAddr = corrAddr;
        this.rnn = rnn;
        this.date = date;
    }
    
    private int idKarta = 0;
    private String incomingNumber = "";
    private String docType = "";
    private String corrAddr = "";
    private String rnn = "";
    private Date date = null;
    
    public String getIncomingNumber(){
        return incomingNumber;
    }
    
    public void setIncomingNumber(String incomingNumber){
        this.incomingNumber = incomingNumber;
    }
    
    public String getDocType(){
        return docType;
    }
    
    public void setDocType(String docType){
        this.docType = docType;
    }
    
    public String getCorrespondence(){
        return corrAddr;
    }
    
    public void setCorrespondence(String corrAddr){
        this.corrAddr = corrAddr;
    }
    
    public String getRnn(){
        return rnn;
    }
    
    public void setRnn(String rnn){
        this.rnn = rnn;
    }
    
    public Date getDate(){
        return date;
    }
    
    public void setDate(Date date){
        this.date = date;
    }
    
    public void setIdKarta(int idKarta){
        this.idKarta = idKarta;
    }
    
    public int getIdKarta(){
        return idKarta;
    }
}
