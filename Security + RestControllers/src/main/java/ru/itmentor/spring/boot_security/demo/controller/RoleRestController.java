package ru.itmentor.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleRestController {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public RoleRestController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        Role role = roleService.getRoleByName(name);
        if (role != null) {
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Set<Role>> getRolesByUserId(@PathVariable long id) {
        User user = userService.readUser(id);
        if (user != null) {
            Set<Role> roles = user.getRoles();
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable("id") long id, @Valid @RequestBody Role role) {
        Role existingRole = roleService.getRoleById(id);
        if (existingRole != null) {
            role.setId(id);
            roleService.createRole(role);
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRole(@PathVariable("name") String roleName) {
        Role role = roleService.getRoleByName(roleName);
        if (role != null) {
            roleService.deleteRole(role);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}