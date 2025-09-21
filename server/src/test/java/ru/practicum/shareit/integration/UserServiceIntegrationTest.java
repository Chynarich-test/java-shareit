package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Test
    void updateUser_Success() {
        UserDto userDto = createTestUser("Вася пупкин", "vasiliy@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("Обновленный васька");
        updateDto.setEmail("vasiliy.updated@example.com");

        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertEquals("Обновленный васька", updatedUser.getName());
        assertEquals("vasiliy.updated@example.com", updatedUser.getEmail());
        assertEquals(userDto.getId(), updatedUser.getId());
    }
}