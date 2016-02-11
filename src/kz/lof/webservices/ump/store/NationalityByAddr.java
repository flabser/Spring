package kz.lof.webservices.ump.store;

public class NationalityByAddr {
    public Address address;

    public NatCount[] getNatCount() {
        return natCount;
    }

    public NationalityByAddr(){}
    public NationalityByAddr(Address address, NatCount[] natCount) {
        this.address = address;
        this.natCount = natCount;
    }

    public void setNatCount(NatCount[] natCount) {
        this.natCount = natCount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public NatCount[] natCount;
}
