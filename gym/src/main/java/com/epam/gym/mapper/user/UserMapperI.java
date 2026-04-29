package com.epam.gym.mapper.user;

import com.epam.gym.dto.CreateTraineeRequestDTO;
import com.epam.gym.dto.UserDTO;
import com.epam.gym.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapperI {

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    User toUser(CreateTraineeRequestDTO dto);

    UserDTO toUserDTO(User user);
}
