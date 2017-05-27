package com.ote.test.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity find(Integer id){
        UserEntity entity = userRepository.findOne(id);
        if (entity == null) {
            throw new NotFoundException(id);
        }
        return entity;
    }

    public UserEntity create(UserEntity userEntity){
        userEntity.setId(null);
        return userRepository.save(userEntity);
    }

    public UserEntity reset(Integer id, UserEntity userEntity){
        if (userRepository.findOne(id) == null) {
            throw new NotFoundException(id);
        }
        userEntity.setId(id);
        return userRepository.save(userEntity);
    }

    public void delete(Integer id){
        if (userRepository.findOne(id) == null) {
            throw new NotFoundException(id);
        }
        userRepository.delete(id);
    }

    public static class NotFoundException extends RuntimeException{
        public NotFoundException(Integer id){
            super("Unable to find user with id " + id);
        }
    }
}
