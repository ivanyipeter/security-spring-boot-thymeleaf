package hu.hibridlevel.test.mvctest.service.role;

import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.util.TestDataCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleDataMapperTest {

    private RoleDataMapper underTest;

    private TestDataCreator testDataCreator;

    @Before
    public void setupUnderTest() {
        underTest = new RoleDataMapper();
        testDataCreator = new TestDataCreator();
    }

    @Test
    public void TestMapRoleDtoFromEntity_shouldReturnCorrentDto_whenCorrectEntityProvided() {

        //Given
        RoleDto exceptedResult = testDataCreator.createRoleDto(1L, "admin");
        Role role = testDataCreator.createRole(1L, "admin");

        //When
        RoleDto actualResult = underTest.mapRoleDtoFromEntity(role);

        //Then
        Assert.assertEquals(actualResult, exceptedResult);
    }

    @Test
    public void testRoleDtoToEntityMapper() {

        //Given
        Role exceptedResult = testDataCreator.createRole(1L, "admin");
        RoleDto roleDto = testDataCreator.createRoleDto(1L, "admin");

        //When
        Role actualResult = underTest.mapRoleEntityFromDto(roleDto);

        //Then
        Assert.assertEquals(actualResult, exceptedResult);
    }

    @Test
    public void testRoleDtoList() {
        //Given
        Role role1 = testDataCreator.createRole(1L, "admin");
        Role role2 = testDataCreator.createRole(1L, "admin");

        List<Role> roleList = new ArrayList<>();
        roleList.add(role1);
        roleList.add(role2);

        RoleDto roleDto1 = testDataCreator.createRoleDto(1L, "admin");
        RoleDto roleDto2 = testDataCreator.createRoleDto(1L, "admin");

        Set<RoleDto> expectedResult = new HashSet<>();
        expectedResult.add(roleDto1);
        expectedResult.add(roleDto2);

        //When
        Set<RoleDto> actualResult = underTest.mapRoleDtoSetFromEntitySet(roleList);

        //Then
        Assert.assertEquals(expectedResult, actualResult);

    }

}



