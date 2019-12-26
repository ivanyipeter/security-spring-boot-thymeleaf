package hu.hibridlevel.test.mvctest.service.user;

import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import hu.hibridlevel.test.mvctest.util.TestDataCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDataMapper userDataMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserService underTest;

    private TestDataCreator testDataCreator;

    @Before
    public void setupUnderTest() {
        underTest = new UserService(userRepository, userDataMapper, roleRepository, bCryptPasswordEncoder);
        testDataCreator = new TestDataCreator();
    }

    @Test
    public void testSaveUser_shouldReturnUser() {
        User exceptedResult = testDataCreator.createUser(1L, "test", "test");

        when(userRepository.save(any(User.class))).thenReturn(exceptedResult);

        User actualResult = userRepository.save(exceptedResult);

        Assert.assertEquals(exceptedResult, actualResult);

    }

    @Test
    public void testFindUserByName_ShouldReturnCorrectOptional_WhenValidDataProvided() {
        //Given
        User user = testDataCreator.createUser(1L, "test", "test");
        Optional<User> expectedResult = Optional.of(user);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(expectedResult);

        //When
        Optional<User> actualResult = underTest.findUserByName("test");

        //Then
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindUserById_shouldReturnCorrectOptional_WhenValidDataProvided() {
        //Given
        User user = testDataCreator.createUser(1L, "test", "test");
        Optional<User> expectedResult = Optional.of(user);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(expectedResult);

        //When
        Optional<User> actualResult = underTest.findUserById(1L);

        //Then
        Assert.assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testUpdateUserRole_shouldReturnUpdatedUserRole_whenValidStringListProvided() {
        //Given
        Role role = testDataCreator.createRole("ADMIN");
        Set<Role> roleSet = testDataCreator.createRoleSet("ADMIN");
        Optional<Role> optionalRole = Optional.of(role);

        User expectedResult = testDataCreator.createUser(1L, "test", "test", roleSet, true);
        User user = testDataCreator.createUser(1L, "test", "test", null, true);

        List<String> newRoles = new ArrayList<>();
        newRoles.add("ADMIN");

        Mockito.when(roleRepository.findRoleByRoleName(Mockito.anyString())).thenReturn(optionalRole);

        //When
        User actualResult = underTest.updateUserRole(user, newRoles);

        //Then
        Assert.assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testCreateNewUser_shouldReturnNewUser_whenValidUserDtoProvided() {
        //Given
        Role role = testDataCreator.createRole("ADMIN");
        Set<Role> roleSet = testDataCreator.createRoleSet("ADMIN");
        Optional<Role> optionalRole = Optional.of(role);

        User expectedResult = testDataCreator.createUser(1L, "test", "test", roleSet, true);

        Set<RoleDto> roleDtoSet = testDataCreator.createRoleDtoSet("ADMIN");
        UserDto userDto = testDataCreator.createUserDto(1L, "test", "test", roleDtoSet, true);

        List<String> newRoles = new ArrayList<>();
        newRoles.add("ADMIN");

        Mockito.when(bCryptPasswordEncoder.encode("test")).thenReturn("test");
        Mockito.when(roleRepository.findRoleByRoleName(Mockito.anyString())).thenReturn(optionalRole);

        //When
        User actualResult = underTest.createNewUser(userDto, newRoles);

        //Then
        Assert.assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testAddRoleToNewUser_shouldReturnUserWithNewRole() {
        //Given
        Role role = testDataCreator.createRole("ADMIN");
        Set<Role> roleSet = testDataCreator.createRoleSet("ADMIN");
        Optional<Role> optionalRole = Optional.of(role);

        User expectedResult = testDataCreator.createUser(1L, "test", "test", roleSet, true);

        List<String> newRoles = new ArrayList<>();
        newRoles.add("ADMIN");

        User user = testDataCreator.createUser(1L, "test", "test", null, true);
        Mockito.when(roleRepository.findRoleByRoleName("ADMIN")).thenReturn(optionalRole);

        //When
        User actualResult = underTest.addRoletoNewUser(newRoles, user);

        //Then
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testUpdateUserActivity() {

        User user = testDataCreator.createUser(1L, "test", "test");
        underTest.updateUserActivity(user, true);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);

    }

}
