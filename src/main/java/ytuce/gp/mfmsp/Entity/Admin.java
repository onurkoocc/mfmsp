package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="admin")
public class Admin extends BaseUser {
    public Admin() {
    }

    public Admin(String firstname, String lastname, String email, String password) {
        super(firstname, lastname, email, password);
    }
}
