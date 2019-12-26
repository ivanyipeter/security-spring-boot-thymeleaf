package hu.hibridlevel.test.mvctest.service.validator;

import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
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
public class RoleDataValidatorTest {

    private final String ROLE_ADMIN = "ADMIN";
    private final String ROLE_USER = "USER";
    private final String VALIDROLE = "NEWROLE";
    private final String INVALIDROLE = "-.,.-,";

    @Mock
    private RoleRepository roleRepository;

    private RoleDataValidator underTest;

    @Before
    public void setupUnderTest() {
        underTest = new RoleDataValidator(roleRepository);
    }

    @Test
    public void testValidateNewRoleReturnsFieldErrorListWhenRoleisNotReservedAndRoleIsInvalidandRoleNotExists(){
        List<FieldError> errors = underTest.validateNewRole(INVALIDROLE);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role name not valid"));

    }

    @Test
    public void testValidateNewRoleReturnsFieldErrorListWhenRoleIsReservedAndRoleIsValidAndRoleNotExists(){
        List<FieldError> errors = underTest.validateNewRole(ROLE_ADMIN);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role is reserved"));

    }

    @Test
    public void testValidateNewRoleReturnsEmptyFieldErrorWhenRoleIsNotReservedAndRoleIsValidAndRoleNotExists() {
        List<FieldError> errors = underTest.validateNewRole(VALIDROLE);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateUpdateRole1000ReturnsFieldErrorListWhenRoleIsNotReservedAndRoleIsInvalid() {
        List<FieldError> errors = underTest.validateUpdateRole1000(INVALIDROLE);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role name not valid"));
    }


    @Test
    public void testValidateUpdateRole1000ReturnsFieldErrorListWhenRoleIsReservedAndRoleIsValid() {
        List<FieldError> errors = underTest.validateUpdateRole1000(ROLE_ADMIN);

        Assertions.assertThat(errors.size()).isEqualTo(2);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role is reserved"));

        Assert.assertTrue(errors.get(1).getDefaultMessage().equals("Role update forbidden"));
    }

    @Test
    public void testValidateUpdateRole1000ReturnsEmptyFieldErrorListWhenRoleIsNotReservedAndRoleIsValid() {
        List<FieldError> errors = underTest.validateUpdateRole1000(VALIDROLE);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testtestCheckIfRoleReservedReturnsEmptyFieldErrorListWhenUnusedRolenameProvided() {
        List<FieldError> errors = new ArrayList<>();

        underTest.checkIfRoleReserved(VALIDROLE, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testCheckIfRoleReservedReturnsFieldErrorListWhenReservedRolenameProvided() {
        List<FieldError> errors = new ArrayList<>();

        underTest.checkIfRoleReserved(ROLE_ADMIN, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role is reserved"));
    }

    @Test
    public void testValidateRolenameReturnsEmptyFieldErrorListWhenValidRolenameProvided() {
        List<FieldError> errors = new ArrayList<>();

        underTest.validateRoleName(VALIDROLE, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateRoleNameReturnsWhithFieldErrorListWhenInvalidRolnameProvided() {
        List<FieldError> errors = new ArrayList<>();

        underTest.validateRoleName(INVALIDROLE, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role name not valid"));

    }

    @Test
    public void testCheckIfRoleExistsReturnsEmptyFieldErrorListWhenUnusedRolenameProvided() {
        List<FieldError> errors = new ArrayList<>();

        Mockito.when(roleRepository.findRoleByRoleName(ROLE_ADMIN)).thenReturn(Optional.of(new Role()));

        underTest.checkIfRoleExists(ROLE_ADMIN, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testValidateDeleteRoleReturnsWithFieldErrorListWhenRoleIsNotPresentAndRoleIsReserved() {
        List<FieldError> errors = underTest.validateDeleteRole(Optional.empty());

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role delete forbidden"));

    }

    @Test
    public void testValidateDeleteRoleReturnsWithFielErrorListWhenRoleIsNotPresent() {
        List<FieldError> errors = underTest.validateDeleteRole(Optional.empty());

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role delete forbidden"));
    }

    @Test
    public void testValidateDeleteRoleReturnsEmptyFieldErrorListWhenRoleIsPresentAndNotReserved() {

        Role role = new Role();
        role.setRole("ADMIN1");

        List<FieldError> errors = underTest.validateDeleteRole(Optional.of(role));

        Assertions.assertThat(errors.size()).isEqualTo(0);

    }

    @Test
    public void testValidateIfNotReservedReturnsWithFieldErrorList() {
        List<FieldError> errors = new ArrayList<>();

        underTest.validateIfNotReserved(ROLE_ADMIN, errors);

        Assertions.assertThat(errors.size()).isEqualTo(1);

        Assert.assertTrue(errors.get(0).getDefaultMessage().equals("Role update forbidden"));
    }

    @Test
    public void testValidateIfNotReservedReturnsEmptyFieldErrorList() {
        List<FieldError> errors = new ArrayList<>();

        underTest.validateIfNotReserved(VALIDROLE, errors);

        Assertions.assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    public void testIsRoleExistReturnsFalseWhenUnusedRolenameProvided() {
        Mockito.when(roleRepository.findRoleByRoleName(VALIDROLE)).thenReturn(Optional.empty());
        Assertions.assertThat(underTest.isRoleExists(VALIDROLE)).isFalse();
    }

    @Test
    public void testIsRoleExistReturnsTrueWhenExistingRolenameProvided() {
        Mockito.when(roleRepository.findRoleByRoleName(ROLE_ADMIN)).thenReturn(Optional.of(new Role()));
        Assertions.assertThat(underTest.isRoleExists(ROLE_ADMIN)).isTrue();
    }

    @Test
    public void testIsRoleReservedReturnsFalseWhenUnusedRolenameProvided() {
        Assertions.assertThat(underTest.isRoleReserved(VALIDROLE)).isFalse();
    }

    @Test
    public void testIsRoleReservedReturnsTrueWhenReservedRolenameProvided() {
        Assertions.assertThat(underTest.isRoleReserved(ROLE_ADMIN)).isTrue();
    }

    @Test
    public void testIsRoleValidReturnFalseWhenInvalidRolenameProvided() {
        Assertions.assertThat(underTest.isRoleValid(INVALIDROLE)).isFalse();
    }

    @Test
    public void testIsRoleValidReturnTrueWhenValidRolenameProvided() {
        Assertions.assertThat(underTest.isRoleValid(VALIDROLE)).isTrue();
    }

}
