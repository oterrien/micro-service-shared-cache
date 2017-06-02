package com.ote.test.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity find(Integer id) {
        UserEntity entity = userRepository.findOne(id);
        if (entity == null) {
            throw new NotFoundException(id);
        }
        return entity;
    }

    public Page<UserEntity> find(Specification<UserEntity> filter, Pageable pageRequest) {
        return userRepository.findAll(filter, pageRequest);
    }

    public UserEntity create(UserEntity userEntity) {
        userEntity.setId(null);
        return userRepository.save(userEntity);
    }

    public UserEntity reset(Integer id, UserEntity userEntity) {
        if (userRepository.findOne(id) == null) {
            throw new NotFoundException(id);
        }
        userEntity.setId(id);
        return userRepository.save(userEntity);
    }

    public void delete(Integer id) {
        if (userRepository.findOne(id) == null) {
            throw new NotFoundException(id);
        }
        userRepository.delete(id);
    }

    public void delete(Specification<UserEntity> filter) {
        userRepository.deleteInBatch(userRepository.findAll(filter));
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(Integer id) {
            super("Unable to find user with id " + id);
        }
    }
}
