package hu.hibridlevel.test.mvctest.repository;

import hu.hibridlevel.test.mvctest.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleById(Long id);

    @Query("Select a FROM Role a Where a.role=:roleName")
    Optional<Role> findRoleByRoleName(@Param("roleName") String roleName);

    @Query("select id from Role u where u.role = :rolename")
    Long findIdByRolename(@Param("rolename") String rolename);


}
