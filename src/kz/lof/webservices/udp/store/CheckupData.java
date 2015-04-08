package kz.lof.webservices.udp.store;
import java.util.Date;

public class CheckupData {
		public String type = "";	
		public String place = "";
		public String checker = "";
		public String ticketNo = "";
		public String certNo = "";
		public String edkNo = "";
		
		public Date date = null;
		
		public CheckupData() {
			
		}
		
		public CheckupData(String type, String place, String checker, 
				String ticketNo, String certNo, String edkNo, Date date) {
			this.type = type;	
			this.place = place;
			this.checker = checker;
			this.ticketNo = ticketNo;
			this.certNo = certNo;
			this.edkNo = edkNo;
			this.date = date;
			
		}

        
        public String getType()
        {
            return type;
        }

        
        public void setType(String type)
        {
            this.type = type;
        }

        
        public String getPlace()
        {
            return place;
        }

        
        public void setPlace(String place)
        {
            this.place = place;
        }

        
        public String getChecker()
        {
            return checker;
        }

        
        public void setChecker(String checker)
        {
            this.checker = checker;
        }

        
        public String getTicketNo()
        {
            return ticketNo;
        }

        
        public void setTicketNo(String ticketNo)
        {
            this.ticketNo = ticketNo;
        }

        
        public String getCertNo()
        {
            return certNo;
        }

        
        public void setCertNo(String certNo)
        {
            this.certNo = certNo;
        }

        
        public String getEdkNo()
        {
            return edkNo;
        }

        
        public void setEdkNo(String edkNo)
        {
            this.edkNo = edkNo;
        }

        
        public Date getDate()
        {
            return date;
        }

        
        public void setDate(Date date)
        {
            this.date = date;
        }
}
