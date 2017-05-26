package com.ote.test;

import com.ote.test.persistence.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {

    public UserPayload convert(UserEntity entity){

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setFirstName(entity.getFirstName());
        payload.setLastName(entity.getLastName());
        return payload;
    }

    public UserEntity convert(UserPayload payload){

        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setFirstName(payload.getFirstName());
        entity.setLastName(payload.getLastName());
        return entity;
    }
}
