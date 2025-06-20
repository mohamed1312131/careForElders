package com.care4elders.event.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceClientTest {

    @Test
    void testGetUserById() {
        com.care4elders.event.client.UserServiceClient client = mock(com.care4elders.event.client.UserServiceClient.class);
        com.care4elders.event.DTO.UserDTO mockUser = new com.care4elders.event.DTO.UserDTO();
        mockUser.setId("user123");
        mockUser.setFirstName("Alice");
        mockUser.setLastName("Smith");
        when(client.getUserById("user123")).thenReturn(mockUser);

        com.care4elders.event.DTO.UserDTO result = client.getUserById("user123");

        assertNotNull(result);
        assertEquals("user123", result.getId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(client, times(1)).getUserById("user123");
    }

    @Test
    void testGetUserById_ReturnsNullWhenUserNotFound() {
        com.care4elders.event.client.UserServiceClient client = mock(com.care4elders.event.client.UserServiceClient.class);
        when(client.getUserById("nonexistent")).thenReturn(null);

        com.care4elders.event.DTO.UserDTO result = client.getUserById("nonexistent");

        assertNull(result);
        verify(client, times(1)).getUserById("nonexistent");
    }

    @Test
    void testGetUserById_WithDifferentUserIds() {
        com.care4elders.event.client.UserServiceClient client = mock(com.care4elders.event.client.UserServiceClient.class);

        com.care4elders.event.DTO.UserDTO userA = new com.care4elders.event.DTO.UserDTO();
        userA.setId("a");
        userA.setFirstName("A");
        userA.setLastName("Alpha");

        com.care4elders.event.DTO.UserDTO userB = new com.care4elders.event.DTO.UserDTO();
        userB.setId("b");
        userB.setFirstName("B");
        userB.setLastName("Beta");

        when(client.getUserById("a")).thenReturn(userA);
        when(client.getUserById("b")).thenReturn(userB);

        com.care4elders.event.DTO.UserDTO resultA = client.getUserById("a");
        com.care4elders.event.DTO.UserDTO resultB = client.getUserById("b");

        assertNotNull(resultA);
        assertEquals("a", resultA.getId());
        assertEquals("A", resultA.getFirstName());
        assertEquals("Alpha", resultA.getLastName());

        assertNotNull(resultB);
        assertEquals("b", resultB.getId());
        assertEquals("B", resultB.getFirstName());
        assertEquals("Beta", resultB.getLastName());

        verify(client, times(1)).getUserById("a");
        verify(client, times(1)).getUserById("b");
    }
}
