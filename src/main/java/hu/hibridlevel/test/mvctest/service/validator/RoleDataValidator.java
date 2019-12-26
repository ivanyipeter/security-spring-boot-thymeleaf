package hu.hibridlevel.test.mvctest.service.validator;

import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleDataValidator {

    private RoleRepository roleRepository;

    @Autowired
    public RoleDataValidator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<FieldError> validateNewRole(String roleName) {
        List<FieldError> errors = new ArrayList<>();

        checkIfRoleReserved(roleName,errors);
        validateRoleName(roleName,errors);
        checkIfRoleExists(roleName,errors);

        return errors;
    }

    public List<FieldError> validateUpdateRole1000(String roleName){
        List<FieldError> errors = new ArrayList<>();

        checkIfRoleReserved(roleName, errors);
        validateIfNotReserved(roleName,errors);
        validateRoleName(roleName,errors);
        checkIfRoleExists(roleName,errors);

        return errors;
    }


    public void checkIfRoleReserved(String roleName, List<FieldError> errors){
        if (isRoleReserved(roleName)) {
            errors.add(new FieldError("role", "role", "Role is reserved"));
        }
    }

    public void validateRoleName(String roleName, List<FieldError> errors){
        if (!isRoleValid(roleName)) {
            errors.add(new FieldError("role", "role", "Role name not valid"));
        }
    }

    public void checkIfRoleExists(String roleName, List<FieldError> errors){
        if (isRoleExists("ROLE_" + roleName)) {
            errors.add(new FieldError("role", "role", "Role name already exists"));
        }
    }

    public List<FieldError> validateRoleUpdate(Optional<Role> role){
        List<FieldError> errors = new ArrayList<>();
        if (role.isEmpty() || isRoleReserved(role.get().getRole())) {
            errors.add(new FieldError("role", "role", "Role update forbidden"));
        }
        return errors;
    }

    public List<FieldError> validateDeleteRole(Optional<Role> role) {
        List<FieldError> errors = new ArrayList<>();

        if (role.isEmpty() || isRoleReserved(role.get().getRole())) {
            errors.add(new FieldError("role", "role", "Role delete forbidden"));
        }
        return errors;
    }

    public void validateIfNotReserved(String roleName, List<FieldError> errors) {
        if (isRoleReserved(roleName)) {
            errors.add(new FieldError("role", "role", "Role update forbidden"));
        }
    }

    public boolean isRoleExists(String roleName) {
        return roleRepository.findRoleByRoleName(roleName).isPresent();
    }

    public boolean isRoleReserved(String roleName) {
        return roleName.toUpperCase().equals("ROLE_ADMIN") || roleName.toUpperCase().equals("ROLE_USER") || roleName.toUpperCase().equals("ADMIN") || roleName.toUpperCase().equals("USER");
    }

    public boolean isRoleValid(String roleName) {
        return null != roleName && !roleName.isBlank() && roleName.length() >= 4 && roleName.matches("[A-Za-z0-9_]+");
    }


}
