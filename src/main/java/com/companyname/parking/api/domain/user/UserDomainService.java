package com.companyname.parking.api.domain.user;

import com.companyname.parking.api.application.util.RandomUtil;
import com.companyname.parking.api.infrastructure.port.adapter.rest.errors.InvalidPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

import static com.companyname.parking.api.application.util.Validation.isEmpty;
import static com.companyname.parking.api.domain.user.User.*;

@Service
public class UserDomainService {

    //Factory method
    public User getRegisteredUser(
            String login,
            String password,
            PasswordEncoder passwordEncoder,
            String firstName,
            String lastName,
            String email,
            String imageUrl,
            String langKey,
            Set<Authority> authorities) throws InvalidPasswordException {
        if (!checkPasswordLength(password)) {
            throw new InvalidPasswordException();
        }

        return new User()
                .setLogin(login.toLowerCase())
                .setPassword(passwordEncoder.encode(password))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setImageUrl(imageUrl)
                .setLangKey(langKey)
                .setActivated(false)
                .setActivationKey(RandomUtil.generateActivationKey())
                .setAuthorities(authorities);
    }

    //Factory method
    public User getUser(
            String login,
            PasswordEncoder passwordEncoder,
            String firstName,
            String lastName,
            String email,
            String imageUrl,
            String langKey,
            Set<Authority> authorities) {
        return new User()
                .setLogin(login)
                .setPassword(passwordEncoder.encode(RandomUtil.generatePassword()))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setImageUrl(imageUrl)
                .setLangKey(isEmpty(langKey) ? UserConstants.DEFAULT_LANGUAGE : langKey)
                .setResetKey(RandomUtil.generateResetKey())
                .setResetDate(Instant.now())
                .setActivated(true)
                .setAuthorities(authorities);
    }

}
