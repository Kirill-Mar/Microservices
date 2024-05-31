package ru.itmentor.spring.boot_security.demo.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter implements Converter<String, Role> {

    @Override
    public Role convert(String roleName) {
        return new Role(roleName);
    }
}
