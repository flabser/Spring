package kz.lof.webservices.ump.store;


public class ReasonData {
    public Address address = new Address();
    public VisitReason[] getIn = {new VisitReason()};
    public  VisitReason[] getOut = {new VisitReason()};
    public int countGetIn = 0;
    public int countGetOut = 0;
}
