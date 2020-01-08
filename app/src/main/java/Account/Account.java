package Account;

import java.io.Serializable;

public abstract class Account implements Serializable {

    // instance
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;

    // constructor
    public Account() {}

    public Account(String id, String firstName, String lastName, String email, String userName,
                   String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    // method
    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public abstract String getType();
}
