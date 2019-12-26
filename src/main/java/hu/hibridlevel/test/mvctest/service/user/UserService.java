package hu.hibridlevel.test.mvctest.service.user;

import hu.hibridlevel.test.mvctest.controller.SecurityController;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    private UserRepository userRepository;
    private UserDataMapper userDataMapper;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDataMapper userDataMapper,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userDataMapper = userDataMapper;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userDataMapper.mapUserDtoListFromEntityList(userRepository.findAll());
    }

    public Optional<User> findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        LOGGER.debug("User find by id: {}", id);
        return user;
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findUserByUsername(name);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
        LOGGER.debug("User deleted: {}", user.getUsername());
    }

    public User updateUserRole(User user, List<String> newRoles) {
        Set<Role> updatedRoleSet = new HashSet<>();

        for (String roleName : newRoles) {
            Optional<Role> role = roleRepository.findRoleByRoleName(roleName);

            if (role.isPresent()) {
                updatedRoleSet.add(role.get());
            }
        }

        user.setRoles(updatedRoleSet);
        userRepository.save(user);

        LOGGER.debug("User role updated: {}");
        return user;
    }

    public void updateUserActivity(User user, boolean active) {
        user.setActive(active);
        userRepository.save(user);
        LOGGER.debug("User activity updated: {}", active);
    }

    public void updateUsername(User user, String name) {
        user.setUsername(name);
        userRepository.save(user);
        LOGGER.debug("Username updated: {}", name);
    }

    public User createNewUser(UserDto userDto, List<String> newRoles) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setActive(userDto.isActive());
        addRoletoNewUser(newRoles, user);
        userRepository.save(user);
        LOGGER.debug("New user saved in database: {} ", user.getUsername());
        return user;
    }

    public User addRoletoNewUser(List<String> newRoles, User user) {
        Set<Role> updatedRoleSet = new HashSet<>();

        for (String roleName : newRoles) {
            Optional<Role> role = roleRepository.findRoleByRoleName(roleName);

            if (role.isPresent()) {
                updatedRoleSet.add(role.get());
            }
        }
        user.setRoles(updatedRoleSet);

        LOGGER.debug("Role added to new user.");
        return user;
    }

}
