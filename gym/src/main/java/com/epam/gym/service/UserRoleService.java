package com.epam.gym.service;

import com.epam.gym.entity.UserRole;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public void merge(Long userId, List<UserRoleEnum> roles) {
        List<UserRole> oldRoles = userRoleRepository.findByUserId(userId);

        for (UserRoleEnum role : roles) {
            if (!containsRole(oldRoles, role)) {
                create(userId, role);
            }
        }
        for (UserRole role : oldRoles) {
            if (!roles.contains(role.getRole())) {
                userRoleRepository.delete(role);
            }
        }
    }


    // TODO:
    //  Please double-check what happens if accidentally called twice:
    //   create(1, ROLE_ADMIN);
    //   create(1, ROLE_ADMIN); DONE
    public void create(Long userId, UserRoleEnum role) {
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setRole(role);
        userRoleEntity.setUserId(userId);
        userRoleRepository.save(userRoleEntity);
    }

    public List<UserRoleEnum> getByProfileId(Long id) {
        List<UserRole> allEntity = userRoleRepository.findByUserId(id);
        List<UserRoleEnum> roles = new LinkedList<>();
        allEntity.forEach(entity -> {
            roles.add(entity.getRole());
        });
        return roles;
    }

    private boolean containsRole(List<UserRole> oldRoleList, UserRoleEnum role) {
        for (UserRole userRole : oldRoleList) {
            if (userRole.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
