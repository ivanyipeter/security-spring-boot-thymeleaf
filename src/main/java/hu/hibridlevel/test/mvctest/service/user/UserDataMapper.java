package hu.hibridlevel.test.mvctest.service.user;

import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import hu.hibridlevel.test.mvctest.service.role.RoleDataMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDataMapper {

    private RoleDataMapper roleDataMapper;
    private UserRepository userRepository;

    @Autowired
    public UserDataMapper(RoleDataMapper roleDataMapper) {
        this.roleDataMapper = roleDataMapper;
    }

    public UserDataMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDataMapper() {
    }

    public UserDto mapUserDtoFromEntity(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setRoles(roleDataMapper.mapRoleDtoSetFromEntitySet(user.getRoles()));
        return userDto;
    }

    public List<UserDto> mapUserDtoListFromEntityList(List<User> userList) {
        List<UserDto> dtoList = new ArrayList<>();

        for (User user : userList) {
            dtoList.add(mapUserDtoFromEntity(user));
        }
        return dtoList;
    }

    public User mapUserEntityFromDto(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    public UserDto mapUserDtoFromEntityByIdOnlyForMockTesting(Long id) {
        User user = userRepository.findById(id).get();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}






