package Service;

import java.io.Serializable;

public class Service implements Serializable{
    //Variables
    private String id;
    private String serviceName;
    private String providerRole;
    private int rate;

    //Constructor
    public Service() {}

    public Service(String id, String serviceName, String providerRole) {
        this.id = id;
        this.serviceName = serviceName;
        this.providerRole = providerRole;
        this.rate = -1; // -1 means not set
    }

    public Service(String id, String serviceName, String providerRole, int rate) {
        this.id = id;
        this.serviceName = serviceName;
        this.providerRole = providerRole;
        this.rate = rate; // -1 means not set
    }

    public String getId() {
        return id;
    }

    public String getProviderRole() {
        return providerRole;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
