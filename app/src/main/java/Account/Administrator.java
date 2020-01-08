package Account;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import Service.Service;

public class Administrator extends Account {

    // constant
    public static final String TYPE = "administrator";

    // constructor
    public Administrator() {}

    public Administrator(String Id, String firstName, String lastName,
                         String email, String userName, String password) {
        super(Id, firstName, lastName, email, userName, password);
    }

    // method
    public String getType() {
        return TYPE;
    }

    public void addService(DatabaseReference databaseServices, String serviceName,
                           String providerRole, int rate) {
        String id = databaseServices.push().getKey();
        Service service = new Service(id, serviceName, providerRole, rate);
        databaseServices.child(id).setValue(service);
    }

    public void editService(DatabaseReference databaseServices, String Id, String newServiceName,
                            String newProviderRole) {
        if (!newServiceName.isEmpty()) {
            databaseServices.child(Id).child("serviceName").setValue(newServiceName);
        }
        if (!newProviderRole.isEmpty()) {
            databaseServices.child(Id).child("providerRole").setValue(newProviderRole);
        }
    }

    public void deleteService(DatabaseReference databaseReference, String id){
        databaseReference.child(id).removeValue();
    }

    public void deleteAccount(DatabaseReference databaseReference, String id){
        databaseReference.child(id).removeValue();
    }
}
