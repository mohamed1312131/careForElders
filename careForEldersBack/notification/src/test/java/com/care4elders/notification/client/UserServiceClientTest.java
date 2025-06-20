package com.care4elders.notification.client;

import com.care4elders.notification.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceClientTest {

    @Test
    void testGetAllUserIds() {
        UserServiceClient client = mock(UserServiceClient.class);
        List<String> mockIds = Arrays.asList("user1", "user2");
        when(client.getAllUserIds()).thenReturn(mockIds);

        List<String> result = client.getAllUserIds();

        assertEquals(2, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        verify(client, times(1)).getAllUserIds();
    }

    @Test
    void testGetUserEmergencyInfo() {
        UserServiceClient client = mock(UserServiceClient.class);
        UserDto mockUser = new UserDto();
        mockUser.setId("user1");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        when(client.getUserEmergencyInfo("user1")).thenReturn(mockUser);

        UserDto result = client.getUserEmergencyInfo("user1");

        assertNotNull(result);
        assertEquals("user1", result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(client, times(1)).getUserEmergencyInfo("user1");
    }
}
