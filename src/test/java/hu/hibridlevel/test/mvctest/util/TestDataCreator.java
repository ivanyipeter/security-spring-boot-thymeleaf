package hu.hibridlevel.test.mvctest.util;

import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;

import java.util.HashSet;
import java.util.Set;

public class TestDataCreator {

    public User createUser(Long id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public User createUser(Long id, String username, String password, boolean active) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(active);
        return user;
    }

    public User createUser(Long id, String username, String password, Set<Role> roles, Boolean active) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        user.setActive(active);
        return user;
    }

    public UserDto createUserDto(Long id, String username, String password, Set<RoleDto> roleDtos, Boolean active) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setRoles(roleDtos);
        userDto.setActive(active);
        return userDto;
    }

    public UserDto createUserDto(Long id, String username, String password, Boolean active) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setActive(active);
        return userDto;
    }

    public Role createRole(String rolename) {
        Role role = new Role();
        role.setRole(rolename);
        return role;
    }

    public Role createRole(Long id, String rolename) {
        Role role = new Role();
        role.setId(id);
        role.setRole(rolename);
        return role;
    }


    public Set<Role> createRoleSet(String rolename) {
        Role role = new Role();
        role.setRole(rolename);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        return roleSet;
    }

    public RoleDto createRoleDto(String rolename) {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole(rolename);
        return roleDto;
    }

    public RoleDto createRoleDto(Long id, String rolename) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(id);
        roleDto.setRole(rolename);
        return roleDto;
    }

    public Set<RoleDto> createRoleDtoSet(String rolename) {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole(rolename);
        Set<RoleDto> roleDtoSet = new HashSet<>();
        roleDtoSet.add(roleDto);
        return roleDtoSet;
    }


}
