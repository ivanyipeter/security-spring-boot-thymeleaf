package hu.hibridlevel.test.mvctest.service.user;


import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.service.role.RoleDataMapper;
import hu.hibridlevel.test.mvctest.util.TestDataCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class UserDataMapperTest {

    @Mock
    private RoleDataMapper roleDataMapper;

    private UserDataMapper underTest;

    private TestDataCreator testDataCreator;

    @Before
    public void setupUnderTest() {
        underTest = new UserDataMapper(roleDataMapper);
        testDataCreator = new TestDataCreator();
    }

    @Test
    public void testMapUserEntityFromDto_shouldReturnCorrectEntity_whenValidDtoProvided() {
        //Given
        User exceptedResult = testDataCreator.createUser(1L,"test","test",true);
        UserDto userDto = testDataCreator.createUserDto(1L,"test","test",true);

        //When
        User actualResult = underTest.mapUserEntityFromDto(userDto);

        //Then
        Assert.assertEquals(exceptedResult, actualResult);
    }

//    @Test
//    public void testMapUserDtoFromEntityByIdOnlyForMockTesting_sholudReturnCorrestUserDto() {
//        //Given
//        UserDto expectedResult = new UserDto();
//        expectedResult.setId(1L);
//        expectedResult.setUsername("Dani");
//        expectedResult.setPassword("Dani");
//        expectedResult.setActive(true);
//
//        //When
//        UserDto actualResult = underTest.mapUserDtoFromEntityByIdOnlyForMockTesting(1L);
//
//        //then
//        Assert.assertEquals(expectedResult, actualResult);
//
//    }

    @Test
    public void testMapUserDtoFromEntity() {
        //Given
        Set<Role> roleSet = testDataCreator.createRoleSet("ADMIN");
        User user = testDataCreator.createUser(1L,"test","test",roleSet,true);

        Set<RoleDto> roleDtoSet = testDataCreator.createRoleDtoSet("ADMIN");
        UserDto exceptedResult = testDataCreator.createUserDto(1L,"test","test",roleDtoSet,true);

        Mockito.when(roleDataMapper.mapRoleDtoSetFromEntitySet(Mockito.any())).thenReturn(roleDtoSet);

        //When
        UserDto actualResult = underTest.mapUserDtoFromEntity(user);

        //Then
        Assert.assertEquals(exceptedResult,actualResult);
    }


    @Test
    public void testMapUserDtoListFromEntityList_sholudReturnCorrectDtoList_whenCorrectEntityListProvided() {
        //Given
        Set<RoleDto> roleDtos = testDataCreator.createRoleDtoSet("ADMIN");
        UserDto userDto = testDataCreator.createUserDto(1L,"test","test",roleDtos,true);
        UserDto userDto2 = testDataCreator.createUserDto(1L,"test2","test2",roleDtos,true);

        List<UserDto> expectedResult = new ArrayList<>();
        expectedResult.add(userDto);
        expectedResult.add(userDto2);

        Set<Role> roleSet = testDataCreator.createRoleSet("ADMIN");
        User user = testDataCreator.createUser(1L,"test","test",roleSet,true);
        User user2 = testDataCreator.createUser(1L,"test2","test2",roleSet,true);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        Mockito.when(roleDataMapper.mapRoleDtoSetFromEntitySet(Mockito.any())).thenReturn(roleDtos);

        //When
        List<UserDto> actualResult = underTest.mapUserDtoListFromEntityList(userList);

        //Then
        Assert.assertEquals(expectedResult, actualResult);

    }


}
