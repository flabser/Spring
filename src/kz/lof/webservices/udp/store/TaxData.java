package kz.lof.webservices.udp.store;

import java.util.Date;

public class TaxData {
    public String privType = "";
	public String docSerial = "";
	public String docNumber = "";
	public String payType = "";
	public String payerRnn = "";
	
	public Date startDate = null;
	public Date endDate = null;
	public Date payDate = null;
	
	public float sum = 0;
	
	public TaxData(){}
	
	public TaxData(String privType, String docSerial, String docNumber, 
				   String payType, String payerRnn, Date startDate, 
				   Date endDate,Date payDate, float sum){
		
		this.privType = privType;
		this.docSerial = docSerial;
		this.docNumber = docNumber;
		this.payType = payType;
		this.payerRnn = payerRnn;
		this.startDate = startDate;
		this.endDate = endDate;
		this.payDate = payDate;
		this.sum = sum;
	}

    
    public String getPrivType()
    {
        return privType;
    }

    
    public void setPrivType(String privType)
    {
        this.privType = privType;
    }

    
    public String getDocSerial()
    {
        return docSerial;
    }

    
    public void setDocSerial(String docSerial)
    {
        this.docSerial = docSerial;
    }

    
    public String getDocNumber()
    {
        return docNumber;
    }

    
    public void setDocNumber(String docNumber)
    {
        this.docNumber = docNumber;
    }

    
    public String getPayType()
    {
        return payType;
    }

    
    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    
    public String getPayerRnn()
    {
        return payerRnn;
    }

    
    public void setPayerRnn(String payerRnn)
    {
        this.payerRnn = payerRnn;
    }

    
    public Date getStartDate()
    {
        return startDate;
    }

    
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    
    public Date getEndDate()
    {
        return endDate;
    }

    
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    
    public Date getPayDate()
    {
        return payDate;
    }

    
    public void setPayDate(Date payDate)
    {
        this.payDate = payDate;
    }

    
    public float getSum()
    {
        return sum;
    }

    
    public void setSum(float sum)
    {
        this.sum = sum;
    }
	
}
