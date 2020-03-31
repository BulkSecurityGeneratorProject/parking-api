package com.companyname.parking.api.infrastructure.port.adapter.rest.vm;

import com.companyname.parking.api.application.user.UserDTO;

import javax.validation.constraints.Size;

import static com.companyname.parking.api.domain.user.UserConstants.PASSWORD_MAX_LENGTH;
import static com.companyname.parking.api.domain.user.UserConstants.PASSWORD_MIN_LENGTH;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
            "} " + super.toString();
    }
}
