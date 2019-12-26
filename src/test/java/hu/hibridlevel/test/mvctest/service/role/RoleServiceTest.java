package hu.hibridlevel.test.mvctest.service.role;


import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.util.TestDataCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleDataMapper roleDataMapper;

    private RoleService underTest;

    private TestDataCreator testDataCreator;

    @Before
    public void setupUnderTest() {
        underTest = new RoleService(roleRepository, roleDataMapper);
        testDataCreator = new TestDataCreator();
    }

    @Test
    public void testFindRoleById_shouldReturnRole_whenValidIdProvided() {
        //Given
        Role role = testDataCreator.createRole("test");
        Optional<Role> expectedResult = Optional.of(role);

        Mockito.when(roleRepository.findRoleById(Mockito.anyLong())).thenReturn(expectedResult);

        //When
        Optional<Role> actualResult = underTest.findRoleById(1l);

        //Then
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testDeleteRoleById_shouldDeleteRole_whenValidIdProvided() {

        underTest.deleteRoleById(1L);

        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(1L);

    }

    @Test
    public void testSaveCustomRole_shouldSaveRoleWithROLE_prefix_whenValidRolenameProvided() {

        underTest.saveCustomRole("ADMIN");
        Role role = testDataCreator.createRole("ROLE_ADMIN");

        Mockito.verify(roleRepository, Mockito.times(1)).save(role);

    }

    @Test
    public void testUpdateRoleName_whenNewRolenameProvided() {

        Role role = testDataCreator.createRole("USER");
        underTest.updateRoleName(role, "ADMIN");

        Role expectedResult = testDataCreator.createRole("ADMIN");

        Mockito.verify(roleRepository, Mockito.times(1)).save(expectedResult);
    }

    @Test
    public void testFindRoleByRoleName_shouldReturnValidRole_whenValidRoleNameProvided() {
        //Given
        Role role = testDataCreator.createRole("test");
        Optional<Role> expectedResult = Optional.of(role);

        Mockito.when(roleRepository.findRoleByRoleName("test")).thenReturn(expectedResult);

        //When
        Optional<Role> actualResult = underTest.findRoleByRoleName("test");

        //Then
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetAllRoles() {

        underTest.getAllRoles();

        Mockito.verify(roleRepository,Mockito.times(1)).findAll();

    }

}
