package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

@Qualifier("GetManyCacheKeyGenerator")
@Component
@Slf4j
public class GetManyCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {

        return target.hashCode() ^
                method.hashCode() ^
                Stream.of(params).
                        filter(Objects::nonNull).
                        map(Object::hashCode).
                        reduce(0, (p1, p2) -> p1 ^ p2);
    }
}