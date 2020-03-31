package com.companyname.parking.api.application.user;

import com.companyname.parking.api.domain.user.User;
import com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.user.UserRepository;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

@Component
public class UserMapperResolver {

    private UserRepository userRepository;

    public UserMapperResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ObjectFactory
    public User resolve(Long userId, @TargetType Class<User> type) {
        return userId != null ? userRepository.getOne(userId) : new User();
    }

}
