package com.companyname.parking.api.application.user;

import lombok.NoArgsConstructor;

/**
 * Constants for Spring Security authorities.
 */
@NoArgsConstructor
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String PARKING_SPOT = "ROLE_PARKING_SPOT";

    public static final String GATE = "ROLE_GATE";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
