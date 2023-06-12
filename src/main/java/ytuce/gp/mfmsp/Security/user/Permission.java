package ytuce.gp.mfmsp.Security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    REPRESENTATIVE_READ("representative:read"),
    REPRESENTATIVE_UPDATE("representative:update"),
    REPRESENTATIVE_CREATE("representative:create"),
    REPRESENTATIVE_DELETE("representative:delete")

    ;

    @Getter
    private final String permission;
}
