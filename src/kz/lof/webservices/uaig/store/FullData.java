package kz.lof.webservices.uaig.store;

import java.util.Date;


public class FullData {
    public FullData(String incomingNumber, Date date, String executor,
            String docType, String corrAddr, String rnn, String legalEntity,
            Date dateAPZ, Date dateAgreement, String firmProj,
            String firmExpert, Date dateExp, Date dateClose, String contentClose){
        this.incomingNumber = incomingNumber;
        this.date = date;
        this.executor = executor;
        this.docType = docType;
        this.corrAddr = corrAddr;
        this.rnn = rnn;
        this.legalEntity = legalEntity;
        this.dateAPZ = dateAPZ;
        this.dateAgreement = dateAgreement;
        this.firmProj = firmProj;
        this.firmExpert = firmExpert;
        this.dateExp = dateExp;
        this.dateClose = dateClose;
        this.contentClose = contentClose;
    }
    
    public FullData(){}
    
    private String incomingNumber = "";//K.NOMERKID
    private Date date = null;          //K.POSTKID
    private String executor = "";      //K.OTDEL 
    private String docType = "";       //V.SPVID
    private String corrAddr = "";      //K.KORKID
    private String rnn = "";           //K.RNN
    private String legalEntity = "";   //K.FACE
    private Date dateAPZ = null;       //K.DATAAPZ
    private Date dateAgreement = null; //K.DATESOGL
    private String firmProj = "";      //K.FIRMPROJEC
    private String firmExpert = "";    //K.FIRMEXP
    private Date dateExp = null;       //K.DATEEXPL
    private Date dateClose = null;     //K.ISPKID
    private String contentClose = "";  //K.SOSTKID
    
    public String getIncomingNumber(){
        return incomingNumber;
    }
    
    public void setIncomingNumber(String incomingNumber){
        this.incomingNumber = incomingNumber;
    }
    
    public Date getDate(){
        return date;
    }
    
    public void setDate(Date date){
        this.date = date;
    }
    
    public String getExecutor(){
        return executor;
    }
    
    public void setExecutor(String executor){
        this.executor = executor;
    }
    
    public String getDocType(){
        return docType;
    }
    
    public void setDocType(String docType)
    {
        this.docType = docType;
    }
    
    public String getCorrAddr()
    {
        return corrAddr;
    }
    
    public void setCorrAddr(String corrAddr)
    {
        this.corrAddr = corrAddr;
    }
    
    public String getRnn()
    {
        return rnn;
    }
    
    public void setRnn(String rnn)
    {
        this.rnn = rnn;
    }
    
    public String getLegalEntity()
    {
        return legalEntity;
    }
    
    public void setLegalEntity(String legalEntity)
    {
        this.legalEntity = legalEntity;
    }
    
    public Date getDateAPZ()
    {
        return dateAPZ;
    }
    
    public void setDateAPZ(Date dateAPZ)
    {
        this.dateAPZ = dateAPZ;
    }
    
    public Date getDateAgreement()
    {
        return dateAgreement;
    }
    
    public void setDateAgreement(Date dateAgreement)
    {
        this.dateAgreement = dateAgreement;
    }
    
    public String getFirmProj()
    {
        return firmProj;
    }
    
    public void setFirmProj(String firmProj)
    {
        this.firmProj = firmProj;
    }
    
    public String getFirmExpert()
    {
        return firmExpert;
    }
    
    public void setFirmExpert(String firmExpert)
    {
        this.firmExpert = firmExpert;
    }
    
    public Date getDateExp()
    {
        return dateExp;
    }
    
    public void setDateExp(Date dateExp)
    {
        this.dateExp = dateExp;
    }
    
    public Date getDateClose()
    {
        return dateClose;
    }
    
    public void setDateClose(Date dateClose)
    {
        this.dateClose = dateClose;
    }
    
    public String getContentClose()
    {
        return contentClose;
    }
    
    public void setContentClose(String contentClose)
    {
        this.contentClose = contentClose;
    }

}
