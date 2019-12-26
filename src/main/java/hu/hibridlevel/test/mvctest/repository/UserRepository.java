package hu.hibridlevel.test.mvctest.repository;

import hu.hibridlevel.test.mvctest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    Optional<User> findUserByUsername(String name);

    @Query("select id from User u where u.username = :username")
    Long findIdByUsername(@Param("username") String username);

}
