package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.exception.BackendResourcesException;
import com.itm.space.backendresources.mapper.UserMapper;
import com.itm.space.backendresources.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplIntegrationTest {

    @Mock
    private Keycloak keycloakClient;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private UserRepresentation userRepresentation;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @Test
    public void createUser_shouldHandleNetworkError() {
        userRequest = new UserRequest("johndoe", "john.doe@example.com", "password", "John", "Doe");
        when(keycloakClient.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        Mockito.doThrow(new BackendResourcesException("Network error", HttpStatus.INTERNAL_SERVER_ERROR))
                .when(usersResource).create(any(UserRepresentation.class));

        BackendResourcesException exception = assertThrows(BackendResourcesException.class, () -> userService.createUser(userRequest));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    public void getUserById_shouldHandleUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(keycloakClient.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(null);

        assertThrows(BackendResourcesException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void createUser_shouldCreateUser() {
        userRequest = new UserRequest("johndoe", "john.doe@example.com", "password", "John", "Doe");
        userResponse = new UserResponse("John", "Doe", "john.doe@example.com", Collections.emptyList(), Collections.emptyList());
        when(keycloakClient.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(null);

        userService.createUser(userRequest);

        Mockito.verify(usersResource).create(any(UserRepresentation.class));
    }

    @Test
    public void getUserById_shouldReturnUserResponse() {
        UUID userId = UUID.randomUUID();
        userResponse = new UserResponse("John", "Doe", "john.doe@example.com", Collections.emptyList(), Collections.emptyList());
        when(keycloakClient.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(userMapper.userRepresentationToUserResponse(any(UserRepresentation.class), any(), any())).thenReturn(userResponse);

        UserResponse response = userService.getUserById(userId);

        assertEquals(userResponse, response);
    }
}
