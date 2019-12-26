package hu.hibridlevel.test.mvctest.controller;

import hu.hibridlevel.test.mvctest.TestConfig;
import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.repository.RoleRepository;
import hu.hibridlevel.test.mvctest.repository.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
//@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Set<Role> createRole(String roleName) {
        Role role = new Role();
        role.setRole(roleName);
        return new HashSet<>(Arrays.asList(role));
    }

    private User createUser(String username, String password, String roleName) {
        String hashedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);
        user.setRoles(createRole(roleName));
        return user;
    }

    @Before
    public void insertUser() {
        User user1 = createUser("admin", "admin", "ADMIN");
        User user2 = createUser("user", "user", "USER");
//        user1.getRoles().forEach(roleRepository::saveAndFlush);
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        Role role = new Role();
        role.setRole("user1234");
        roleRepository.saveAndFlush(role);
    }

    @After
    public void deleteAllUsers() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
  @WithMockUser(roles = "ADMIN")
    public void testUserList_shouldReturnUserList_whenValidRequest() throws Exception {
        mockMvc.perform(get("/admin/userlist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("userlist"))
                .andExpect(model().attribute("userlist", hasSize(2)))
                .andExpect(model().attribute("userlist", hasItem(
                        allOf(
                                hasProperty("id"),
                                hasProperty("username"),
                                hasProperty("password"),
                                hasProperty("active"),
                                hasProperty("roles")
                        )
                )))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUser_sholudUpdateUserDataAndReturnToUpdateUserPage_whenValidIdProvided() throws Exception {
        Long id = userRepository.findIdByUsername("admin");

        mockMvc.perform(get("/admin/updateuser/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("updateuser"))
                .andExpect(model().attribute("rolelist", hasSize(3)))
                .andExpect(model().attribute("name", "admin"))
                .andExpect(view().name("updateuser"))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateUserUserName_shouldReturnError_whenInValidUsernameProvided() throws Exception {
        Long id = userRepository.findIdByUsername("admin");
        String name = "a";

        mockMvc.perform(post("/admin/updateuser/updateusername/{id}", id)
                .param("name", name))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attributeCount(1))
                .andExpect(view().name("redirect:/admin/updateuser/" + id))
                .andExpect(redirectedUrl("/admin/updateuser/" + id))
                .andExpect(status().is(302))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateUserUserName_shouldUpdateUsername_whenValidUsernameProvided() throws Exception {
        Long id = userRepository.findIdByUsername("admin");
        String name = "user1234";

        mockMvc.perform(post("/admin/updateuser/updateusername/{id}", id)
                .param("name", name))
                .andExpect(flash().attribute("message", "Username successfully updated"))
                .andExpect(flash().attributeCount(1))
                .andExpect(view().name("redirect:/admin/updateuser/" + id))
                .andExpect(redirectedUrl("/admin/updateuser/" + id))
                .andExpect(status().is(302));

        Optional<User> user = userRepository.findById(id);
        Assert.assertEquals("user1234", user.get().getUsername());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserActive_shoudReturnValidActivity() throws Exception {
        Long id = userRepository.findIdByUsername("admin");

        mockMvc.perform(post("/admin/updateuser/updateuseractive/" + id)
                .param("activated", "false"))
                .andExpect(flash().attribute("message", "Role activity updated"))
                .andExpect(view().name("redirect:/admin/updateuser/" + id))
                .andExpect(redirectedUrl("/admin/updateuser/" + id))
                .andExpect(status().is(302));

        Optional<User> user = userRepository.findById(id);
        Assert.assertFalse(user.get().isActive());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserRole_shouldUpdateRole_whenValidRoleProvided() throws Exception {
        Long id = userRepository.findIdByUsername("user");
        Optional<User> user = userRepository.findById(id);

        mockMvc.perform(post("/admin/updateuser/updateuserrole/{id}", id)
                .param("roles", "ADMIN"))
                .andExpect(flash().attribute("message", "User role successfully updated."))
                .andExpect(view().name("redirect:/admin/updateuser/" + id))
                .andExpect(redirectedUrl("/admin/updateuser/" + id))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateNewUser_get() throws Exception {
        mockMvc.perform(get("/admin/createnewuser"))
                .andExpect(model().attributeExists("rolelist"))
                .andExpect(model().attribute("rolelist", hasItem(allOf(
                        hasProperty("id"),
                        hasProperty("role")
                ))))
                .andExpect(view().name("createnewuser"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateNewUser_shouldReturnNewuser_whenValidDataProvided() throws Exception {
        UserDto userDto = new UserDto(1L, "test123", "test123", true);

        mockMvc.perform(post("/admin/createnewuser")
                .param("username", userDto.getUsername())
                .param("password", userDto.getPassword())
                .param("chosenroles", "admin"))
                .andExpect(flash().attribute("message", "User created succesfully"))
                .andExpect(flash().attributeCount(1))
                .andExpect(view().name("redirect:/admin/createnewuser"))
                .andExpect(redirectedUrl("/admin/createnewuser"))
                .andExpect(status().isFound()).andReturn();

        Optional<User> user = userRepository.findUserByUsername("test123");
        Assert.assertTrue(user.isPresent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateNewUser_shouldReturnError_wheninValidDataProvided() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test", true);

        mockMvc.perform(post("/admin/createnewuser")
                .param("username", userDto.getUsername())
                .param("password", userDto.getPassword())
                .param("chosenroles", "admin"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attributeCount(1))
                .andExpect(view().name("redirect:/admin/createnewuser"))
                .andExpect(redirectedUrl("/admin/createnewuser"))
                .andExpect(status().isFound()).andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteUser_shouldDeleteUser_whenPrincipalIsNotUserOrAdmin() throws Exception {
        Long id = userRepository.findIdByUsername("user");

        mockMvc.perform(get("/admin/deleteuser/{id}", id))
                .andExpect(flash().attribute("message", "User successfully deleted."))
                .andExpect(status().is(302));
        Optional<User> user = userRepository.findById(id);
        Assert.assertTrue(user.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateNewRole_shouldCreateNewRole_whenValidRolenameProvided() throws Exception {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("ADMIN123");

        mockMvc.perform(post("/admin/createnewrole")
                .param("role", roleDto.getRole()))
                .andExpect(flash().attribute("message", "Role created succesfully"))
                .andExpect(view().name("redirect:/admin/createnewrole"))
                .andExpect(redirectedUrl("/admin/createnewrole"))
                .andExpect(status().is(302));

        Optional<Role> role = roleRepository.findRoleByRoleName("ROLE_ADMIN123");
        Assert.assertTrue(role.isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateNewRole_shouldReturnError_whenInValidRolenameProvided() throws Exception {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("ADMIN");

        mockMvc.perform(post("/admin/createnewrole")
                .param("role", roleDto.getRole()))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attributeCount(1))
                .andExpect(view().name("redirect:/admin/createnewrole"))
                .andExpect(redirectedUrl("/admin/createnewrole"))
                .andExpect(status().is(302));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testRoleList_shouldReturnRoleList() throws Exception {
        mockMvc.perform(get("/admin/rolelist"))
                .andExpect(view().name("rolelist"))
                .andExpect(model().attribute("rolelist", hasSize(3)))
                .andExpect(model().attribute("rolelist", hasItem
                        (allOf(
                                hasProperty("id"),
                                hasProperty("role")))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteRole_shouldDeleteRole_whenValidRoleNameProvided() throws Exception {
        Long id = roleRepository.findIdByRolename("user1234");

        mockMvc.perform(get("/admin/deleterole/{id}", id))
                .andExpect(flash().attribute("message", "Role deleted successfully"))
                .andExpect(view().name("redirect:/admin/rolelist/"))
                .andExpect(redirectedUrl("/admin/rolelist/"))
                .andExpect(status().is(302));

        Assert.assertTrue(roleRepository.findRoleByRoleName("user1234").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteRole_shouldReturnError_whenInValidRoleNameProvided() throws Exception {
        Long id = roleRepository.findIdByRolename("ADMIN");

        mockMvc.perform(get("/admin/deleterole/{id}", id))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(view().name("redirect:/admin/rolelist/"))
                .andExpect(redirectedUrl("/admin/rolelist/"))
                .andExpect(status().is(302));

        Assert.assertTrue(roleRepository.findRoleByRoleName("user1234").isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateRole_shouldReturnUpdatedRole_whenValidRolenameProvide() throws Exception {
        Long id = roleRepository.findIdByRolename("user1234");

        mockMvc.perform(post("/admin/updaterole/{id}", id)
                .param("rolename", "admin1234"))
                .andExpect(flash().attribute("message", "Rolename updated"))
                .andExpect(view().name("redirect:/admin/updaterole/" + id))
                .andExpect(redirectedUrl("/admin/updaterole/" + id))
                .andExpect(status().is(302));

        Assert.assertTrue(roleRepository.findRoleByRoleName("ADMIN1234").isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateRole_shouldReturnErrors_whenInValidRolenameProvide() throws Exception {
        Long id = roleRepository.findIdByRolename("user1234");

        mockMvc.perform(post("/admin/updaterole/{id}", id)
                .param("rolename", "admin"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(view().name("redirect:/admin/updaterole/" + id))
                .andExpect(redirectedUrl("/admin/updaterole/" + id))
                .andExpect(status().is(302));

        Assert.assertTrue(roleRepository.findRoleByRoleName("user1234").isPresent());

    }

}




