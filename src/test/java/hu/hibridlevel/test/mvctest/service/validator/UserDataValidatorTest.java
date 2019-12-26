package hu.hibridlevel.test.mvctest.service.validator;

import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import hu.hibridlevel.test.mvctest.util.TestDataCreator;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserDataValidatorTest {

    private final String VALID_USERNAME = "user1234";
    private final String VALID_USERNAME_2 = "user5678";
    private final String VALID_PASSWORD = "admin1234";
    private final String INVALID_USERNAME = ",-,-.,-";
    private final String INVALID_PASSWORD = "ÄÄÄÄÄÄ";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    private TestDataCreator testDataCreator;
    private UserDataValidator underTest;

    @Before
    public void setupUnderTest() {
        underTest = new UserDataValidator(userRepository, roleRepository);
        testDataCreator = new TestDataCreator();
    }

    @Test
    public void testValidateNewUserDataReturnsFieldErrorListWhenValidUsernameandPasswordAndRoleSelectedAndUsernameExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.of(new User()));
        List<FieldError> errors = underTest.validateNewUserData(VALID_USERNAME, VALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assertions.assertThat(errors.get(0).getDefaultMessage().equals("Username already exists"));

    }


    @Test
    public void testValidateNewUserDataReturnsFieldErrorListWhenInValidUsernameandPasswordAndNoRoleSelectedAndUsernameNotExists() {
        List<String> roles = new ArrayList<>();

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateNewUserData(INVALID_USERNAME, INVALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(3);

        Assertions.assertThat(errors.get(0).getDefaultMessage().equals("Username not valid"));
        Assertions.assertThat(errors.get(1).getDefaultMessage().equals("Password not valid"));
        Assertions.assertThat(errors.get(2).getDefaultMessage().equals("No role selected"));

    }

    @Test
    public void testValidateNewUserDataReturnsFieldErrorListWhenInValidUsernameandPasswordAndValidRoleProvidedAndUsernameNotExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateNewUserData(INVALID_USERNAME, INVALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(2);

        Assertions.assertThat(errors.get(0).getDefaultMessage().equals("Username not valid"));
        Assertions.assertThat(errors.get(1).getDefaultMessage().equals("Password not valid"));

    }

    @Test
    public void testValidateNewUserDataReturnsFieldErrorListWhenValidUsernameAndInValidPasswordAndRoleProvidedAndUsernameNotExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateNewUserData(VALID_USERNAME, INVALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assertions.assertThat(errors.get(0).getDefaultMessage().equals("Password not valid"));
    }

    @Test
    public void testValidateNewUserDataReturnsFieldErrorListWhenInValidUsernameandValidPasswordAndRoleProvidedAndUsernameNotExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateNewUserData(INVALID_USERNAME, VALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assertions.assertThat(errors.get(0).getDefaultMessage().equals("Username not valid"));
    }

    @Test
    public void testValidateNewUserDataReturnsEmptyFieldErrorListWhenValidUsernamePasswordRoleProvidedAndUsernameNotExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateNewUserData(VALID_USERNAME, VALID_PASSWORD, roles);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateUpdateUsernameReturnsFieldErrorListWhenInValidUsernameProvidedAndUsernameNotExists() {
        Mockito.when(userRepository.findUserByUsername(INVALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateUpdateUsername(INVALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Username not valid"));

    }

    @Test
    public void testValidateUpdateUsernameReturnsFieldErrorListWhenValidUsernameProvidedAndUsernameNotExists() {
        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        List<FieldError> errors = underTest.validateUpdateUsername(VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateUpdateUsernameReturnsFieldErrorListWhenValidUsernameProvidedAndUsernameExists() {
        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.of(new User()));
        List<FieldError> errors = underTest.validateUpdateUsername(VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Username already exists"));
    }

    @Test
    public void testValidateUpdateUserRoleReturnsFieldErrorListWhenRoleSelected() {
        List<String> roles = new ArrayList<>();
        List<FieldError> errors = underTest.validateUpdateUserRole(roles);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("No role selected"));

    }

    @Test
    public void testValidateUpdateUserRoleReturnsEmptyFieldErrorListWhenRoleSelected() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN123");

        Mockito.when(roleRepository.findRoleByRoleName(Mockito.anyString())).thenReturn(Optional.of(new Role()));

        List<FieldError> errors = underTest.validateUpdateUserRole(roles);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateUserDeleteReturnsFieldErrorListWhenUsernameAndPrincipalIsTheSameAndInValidUserProvided() {
        List<FieldError> errors = underTest.validateUserDelete(Optional.empty(), VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Delete forbidden"));
    }

    @Test
    public void testValidateUserDeleteReturnsFieldErrorListWhenUsernameAndPrincipalIsTheSameAndValidUserProvided() {
        User user = testDataCreator.createUser(1L, "user1234", "asdasd");
        List<FieldError> errors = underTest.validateUserDelete(Optional.of(user), VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Delete forbidden"));
    }

    @Test
    public void testValidateUserDeleteReturnsEmptyFieldErrorListWhenUsernameAndPrincipalIsDifferentAndValidUserProvided() {
        User user = testDataCreator.createUser(1L, "asdasd", "asdasd");
        List<FieldError> errors = underTest.validateUserDelete(Optional.of(user), VALID_USERNAME_2);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateUserUpdateReturnsFieldErrorListWhenUsernameAndPrincipalIsTheSameAndInValidUserProvided() {
        List<FieldError> errors = underTest.validateUserUpdate(Optional.empty(), VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Update forbidden"));

    }

    @Test
    public void testValidateUserUpdateReturnsFieldErrorListWhenUsernameAndPrincipalIsTheSameAndValidUserProvided() {
        User user = testDataCreator.createUser(1L, "user1234", "asdasd");

        List<FieldError> errors = underTest.validateUserUpdate(Optional.of(user), VALID_USERNAME);

        Assertions.assertThat(errors.size()).isEqualTo(1);
        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Update forbidden"));

    }

    @Test
    public void testValidateUserUpdateReturnsFieldErrorListWhenUsernameAndPrincipalIsDifferentAndInValidUserProvided() {
        List<FieldError> errors = underTest.validateUserUpdate(Optional.empty(), VALID_USERNAME_2);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Update forbidden"));

    }

    @Test
    public void testValidateUserUpdateReturnsEmptyFieldErrorListWhenUsernameAndPrincipalIsDifferentAndValidUserProvided() {
        User user = testDataCreator.createUser(1L, "asdasd", "asdasd");

        List<FieldError> errors = underTest.validateUserUpdate(Optional.of(user), VALID_USERNAME_2);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateUserDataReturnsTrueWhenUsernameAndLoggedInUsernameIsDifferent() {
        Assertions.assertThat(underTest.validateUserData(VALID_USERNAME, VALID_USERNAME_2)).isFalse();
    }

    @Test
    public void testValidateUserDataReturnsTrueWhenUsernameAndLoggedInUsernameIstheSame() {
        Assertions.assertThat(underTest.validateUserData(VALID_USERNAME, VALID_USERNAME)).isTrue();
    }

    @Test
    public void testIsUsernameExistReturnsFieldErrorListWhenUsernameExists() {
        List<FieldError> errors = new ArrayList<>();
        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.of(new User()));
        underTest.isUsernameExist(VALID_USERNAME, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

    }

    @Test
    public void testIsUsernameExistReturnsEmptyFieldErrorListWhenUsernameNotExists() {
        List<FieldError> errors = new ArrayList<>();
        Mockito.when(userRepository.findUserByUsername(VALID_USERNAME)).thenReturn(Optional.empty());
        underTest.isUsernameExist(VALID_USERNAME, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateUsernameReturnsFieldErrorListWhenInValidUsernameProvided() {
        List<FieldError> errors = new ArrayList<>();
        underTest.validateUsername(INVALID_USERNAME, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

    }

    @Test
    public void testValidateUsernameReturnsEmptyFieldErrorListWhenValidUsernameProvided() {
        List<FieldError> errors = new ArrayList<>();
        underTest.validateUsername(VALID_USERNAME, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidatePasswordReturnsFieldErrorListWhenInvalidPasswordProvided() {
        List<FieldError> errors = new ArrayList<>();
        underTest.validatePassword(INVALID_PASSWORD, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Password not valid"));
    }

    @Test
    public void testValidatePasswordReturnsEmptyFieldErrorListWhenValidPasswordProvided() {
        List<FieldError> errors = new ArrayList<>();
        underTest.validatePassword(VALID_PASSWORD, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateRoleReturnsEmptyFieldErrorListWhenRoleListProvided() {
        List<String> roleList = new ArrayList<>();
        roleList.add("ADMIN");

        List<FieldError> errors = new ArrayList<>();
        underTest.validateRole(roleList, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateRoleReturnsFieldErrorListWhenEmptyRoleListProvided() {
        List<String> roleList = new ArrayList<>();
        List<FieldError> errors = new ArrayList<>();
        underTest.validateRole(roleList, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("No role selected"));
    }

    @Test
    public void testCheckIfUsernameExistsReturnsFalseWhenUserNotExists() {
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThat(underTest.checkIfUsernameExists("user1234")).isFalse();

    }

    @Test
    public void testCheckIfUsernameExistsReturnsTrueWhenUserExists() {
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(new User()));
        Assertions.assertThat(underTest.checkIfUsernameExists("user1234")).isTrue();
    }

    @Test
    public void testIsPasswordValidReturnsFalseWhenInvadlidPasswordProvided() {
        Assertions.assertThat(underTest.isPasswordValid(INVALID_PASSWORD)).isFalse();
    }

    @Test
    public void testIsPasswordValidReturnsTrueWhenValidPasswordProvided() {
        Assertions.assertThat(underTest.isPasswordValid(VALID_PASSWORD)).isTrue();
    }

}
