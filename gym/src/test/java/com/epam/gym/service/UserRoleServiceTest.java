package com.epam.gym.service;

import com.epam.gym.entity.UserRole;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    void merge_shouldDeleteOldRoles_notInNewList() {
        Long userId = 1L;

        UserRole oldRole = new UserRole();
        oldRole.setUserId(userId);
        oldRole.setRole(UserRoleEnum.ROLE_ADMIN);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(oldRole));

        userRoleService.merge(userId, List.of());

        verify(userRoleRepository).delete(oldRole);
    }

    @Test
    void merge_shouldCreateAndDeleteCorrectly() {
        Long userId = 1L;

        UserRole oldRole = new UserRole();
        oldRole.setUserId(userId);
        oldRole.setRole(UserRoleEnum.ROLE_ADMIN);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(oldRole));

        userRoleService.merge(userId, List.of(UserRoleEnum.ROLE_TRAINEE));

        verify(userRoleRepository).save(any());
        verify(userRoleRepository).delete(oldRole);
    }

    @Test
    void merge_shouldCreateAllRoles_whenNoOldRoles() {
        Long userId = 1L;

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of());

        userRoleService.merge(userId,
                List.of(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_TRAINER));

        verify(userRoleRepository, times(2)).save(any());
        verify(userRoleRepository, never()).delete(any());
    }

    @Test
    void merge_shouldDeleteAll_whenNewRolesEmpty() {
        Long userId = 1L;

        UserRole role1 = new UserRole();
        role1.setRole(UserRoleEnum.ROLE_ADMIN);

        UserRole role2 = new UserRole();
        role2.setRole(UserRoleEnum.ROLE_TRAINEE);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(role1, role2));

        userRoleService.merge(userId, List.of());

        verify(userRoleRepository).delete(role1);
        verify(userRoleRepository).delete(role2);
    }

    @Test
    void create_shouldAllowDuplicateRoles() {
        Long userId = 1L;

        userRoleService.create(userId, UserRoleEnum.ROLE_ADMIN);
        userRoleService.create(userId, UserRoleEnum.ROLE_ADMIN);

        verify(userRoleRepository, times(2)).save(any());
    }

    @Test
    void getByProfileId_shouldHandleNullRole() {
        Long userId = 1L;

        UserRole role = new UserRole();
        role.setUserId(userId);
        role.setRole(null);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(role));

        List<UserRoleEnum> result =
                userRoleService.getByProfileId(userId);

        assertEquals(1, result.size());
        assertNull(result.get(0));
    }

    @Test
    void getByProfileId_shouldThrow_whenRepositoryReturnsNull() {
        when(userRoleRepository.findByUserId(1L))
                .thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> userRoleService.getByProfileId(1L));
    }

    @Test
    void merge_shouldNotCreate_whenRoleAlreadyExists() {
        Long userId = 1L;

        UserRole existing = new UserRole();
        existing.setUserId(userId);
        existing.setRole(UserRoleEnum.ROLE_ADMIN);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(existing));

        userRoleService.merge(userId, List.of(UserRoleEnum.ROLE_ADMIN));

        verify(userRoleRepository, never()).save(any());
    }

    @Test
    void merge_shouldCallCreateForEachRole() {
        Long userId = 1L;
        List<UserRoleEnum> roles = List.of(
                UserRoleEnum.ROLE_TRAINEE,
                UserRoleEnum.ROLE_ADMIN
        );

        userRoleService.merge(userId, roles);

        verify(userRoleRepository, times(2))
                .save(Mockito.<UserRole>any());
    }

    @Test
    void create_shouldSaveUserRole() {
        Long userId = 1L;
        UserRoleEnum role = UserRoleEnum.ROLE_TRAINER;

        userRoleService.create(userId, role);

        verify(userRoleRepository).save(argThat(userRole ->
                userRole.getUserId().equals(userId) &&
                        userRole.getRole().equals(role)
        ));
    }

    @Test
    void getByProfileId_shouldReturnRoleList() {
        Long userId = 1L;

        UserRole role1 = new UserRole();
        role1.setUserId(userId);
        role1.setRole(UserRoleEnum.ROLE_TRAINEE);

        UserRole role2 = new UserRole();
        role2.setUserId(userId);
        role2.setRole(UserRoleEnum.ROLE_ADMIN);

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of(role1, role2));

        List<UserRoleEnum> result =
                userRoleService.getByProfileId(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(UserRoleEnum.ROLE_TRAINEE));
        assertTrue(result.contains(UserRoleEnum.ROLE_ADMIN));
    }

    @Test
    void getByProfileId_shouldReturnEmptyList_whenNoRoles() {
        Long userId = 1L;

        when(userRoleRepository.findByUserId(userId))
                .thenReturn(List.of());

        List<UserRoleEnum> result =
                userRoleService.getByProfileId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}