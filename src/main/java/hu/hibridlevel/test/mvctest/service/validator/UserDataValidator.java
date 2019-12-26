package hu.hibridlevel.test.mvctest.service.validator;

import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDataValidator {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserDataValidator(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<FieldError> validateNewUserData(String username, String password, List<String> roles) {
        List<FieldError> errors = new ArrayList<>();

        validateUsername(username, errors);
        validatePassword(password, errors);
        validateRole(roles, errors);
        isUsernameExist(username, errors);

        return errors;
    }

    public List<FieldError> validateUpdateUsername(String username) {
        List<FieldError> errors = new ArrayList<>();

        validateUsername(username, errors);
        isUsernameExist(username, errors);

        return errors;
    }


    public List<FieldError> validateUpdateUserRole(List<String> roles) {
        List<FieldError> errors = new ArrayList<>();
        if (roles.isEmpty()) {
            errors.add(new FieldError("user", "roles", "No role selected"));
        } else {
            for (String roleName : roles) {
                Optional<Role> role = roleRepository.findRoleByRoleName(roleName);
                if (!(role.isPresent())) {
                    errors.add(new FieldError("user", "roles", "No role selected"));
                }
            }
        }
        return errors;
    }

    public List<FieldError> validateUserDelete(Optional<User> user, String principal) {
        List<FieldError> errors = new ArrayList<>();

        if (user.isEmpty() || validateUserData(user.get().getUsername(), principal)) {
            errors.add(new FieldError("", "", "Delete forbidden"));
        }
        return errors;
    }

    public List<FieldError> validateUserUpdate(Optional<User> user, String principal) {
        List<FieldError> errors = new ArrayList<>();

        if (user.isEmpty() || validateUserData(user.get().getUsername(), principal)) {
            errors.add(new FieldError("", "", "Update forbidden"));
        }
        return errors;
    }

    public Boolean validateUserData(String username, String loggedInUser) {
        return username.equals(loggedInUser);
    }

    public List<FieldError> isUsernameExist(String newUsername, List<FieldError> errors) {
        if (checkIfUsernameExists(newUsername)) {
            errors.add(new FieldError("", "", "Username already exists"));
        }
        return errors;
    }

    public void validateUsername(String username, List<FieldError> errors) {
        if (!isUsernameValid(username)) {
            errors.add(new FieldError("user", "username", "Username not valid"));
        }
    }

    public void validatePassword(String password, List<FieldError> errors) {
        if (!isPasswordValid(password)) {
            errors.add(new FieldError("user", "password", "Password not valid"));
        }
    }

    public void validateRole(List<String> roles, List<FieldError> errors) {
        if (roles.isEmpty()) {
            errors.add(new FieldError("user", "roles", "No role selected"));
        }
    }

    public Boolean checkIfUsernameExists(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    public Boolean isUsernameValid(String username) {
        return null != username && !username.isBlank() && username.length() > 5 && username.matches("[A-Za-z0-9]{5,}");
    }

    public Boolean isPasswordValid(String password) {
        return null != password && !password.isBlank() && password.length() > 5 && password.matches("[A-Za-z0-9_\\p{Punct}]+");
    }
}



