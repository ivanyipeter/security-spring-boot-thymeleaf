package hu.hibridlevel.test.mvctest.service.role;

import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private RoleDataMapper roleDataMapper;
    private final String ROLE = "ROLE_";

    @Autowired
    public RoleService(RoleRepository roleRepository,
                       RoleDataMapper roleDataMapper) {
        this.roleRepository = roleRepository;
        this.roleDataMapper = roleDataMapper;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findRoleById(id);
    }

    public Optional<Role> findRoleByRoleName(String name) {
        return roleRepository.findRoleByRoleName(name);
    }

    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }

    public void saveCustomRole(String roleName) {
        Role role = new Role();
        role.setRole(ROLE + roleName.toUpperCase());
        roleRepository.save(role);
    }

    public void updateRoleName(Role role, String roleName) {
        role.setRole(roleName.toUpperCase());
        roleRepository.save(role);
    }

}
