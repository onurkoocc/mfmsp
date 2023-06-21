package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.autoconfigure.web.WebProperties;
import ytuce.gp.mfmsp.Constants.AccessTokenName;

@Entity
@Table(name = "access_token")
@Data
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    private String value;

}
