package com.ote.test;

import com.ote.test.persistence.UserEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapperService {

    public UserPayload convert(UserEntity entity) {

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setFirstName(entity.getFirstName());
        payload.setLastName(entity.getLastName());
        return payload;
    }

    public UserEntity convert(UserPayload payload) {

        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setFirstName(payload.getFirstName());
        entity.setLastName(payload.getLastName());
        return entity;
    }

    private Sort getSort(String sortingBy, String sortingDirection) {

        Sort.Order orderByPrimaryKeyAsc = new Sort.Order(Sort.Direction.ASC, "id");

        if (StringUtils.isEmpty(sortingBy)) {
            return new Sort(orderByPrimaryKeyAsc);
        }

        Sort.Direction sortingDirectionEnum = "ASC".equals(sortingDirection) ? Sort.Direction.ASC : Sort.Direction.ASC;

        Sort.Order orderByPropertyAndDirection = new Sort.Order(sortingDirectionEnum, sortingBy);

        if ("id".equalsIgnoreCase(sortingBy)) {
            return new Sort(orderByPropertyAndDirection);
        }

        return new Sort(orderByPropertyAndDirection, orderByPrimaryKeyAsc);
    }

    public Pageable getPageRequest(String sortingBy, String sortingDirection, int pageSize, int pageIndex) {

        return new PageRequest(pageIndex, pageSize, getSort(sortingBy, sortingDirection));
    }

    public Specification<UserEntity> getFilter(UserPayload filter) {

        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getFirstName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("firstName"), filter.getFirstName()));
            }

            if (filter.getLastName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("lastName"), filter.getLastName()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
