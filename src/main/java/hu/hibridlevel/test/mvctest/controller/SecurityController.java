package hu.hibridlevel.test.mvctest.controller;

import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.dto.UserDto;
import hu.hibridlevel.test.mvctest.model.Role;
import hu.hibridlevel.test.mvctest.model.User;
import hu.hibridlevel.test.mvctest.service.role.RoleService;
import hu.hibridlevel.test.mvctest.service.user.UserService;
import hu.hibridlevel.test.mvctest.service.validator.RoleDataValidator;
import hu.hibridlevel.test.mvctest.service.validator.UserDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    private UserService userService;
    private RoleService roleService;
    private UserDataValidator userDataValidator;
    private RoleDataValidator roleDataValidator;


    @Autowired
    public SecurityController(UserService userService,
                              RoleService roleService,
                              UserDataValidator userDataValidator,
                              BCryptPasswordEncoder bCryptPasswordEncoder,
                              RoleDataValidator roleDataValidator) {
        this.userService = userService;
        this.roleService = roleService;
        this.userDataValidator = userDataValidator;
        this.roleDataValidator = roleDataValidator;
    }

    @GetMapping("/")
    public String homePage() {
        LOGGER.info("homepage");

        return "home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        String errorMessage = null;

        if (error != null) {
            errorMessage = "Username or Password is invalid";
        }

        model.addAttribute("errorMessge", errorMessage);

        return "login";
    }


    @GetMapping("/admin/userlist")
    public String userList(Model model) {
        List<UserDto> userList = userService.getAllUsers();
        model.addAttribute("userlist", userList);

        LOGGER.info("User list page displayed.");

        return "userlist";
    }

    @GetMapping("/admin/updateuser/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model,
                             Principal principal, RedirectAttributes attributes) {
        Optional<User> user = userService.findUserById(id);

        List<FieldError> errors = userDataValidator.validateUserUpdate(user, principal.getName());

        if (errors.isEmpty()) {
            model.addAttribute("rolelist", roleService.getAllRoles());
            model.addAttribute("name", user.get().getUsername());

            LOGGER.info("Update user page displayed.");

            return "updateuser";

        } else {
            attributes.addFlashAttribute("errors", errors);
            return "redirect:/admin/userlist";
        }

    }

    @PostMapping("/admin/updateuser/updateusername/{id}")
    public String updateUsername(@PathVariable("id") Long id, @RequestParam("name") String name, RedirectAttributes attributes) {
        Optional<User> user = userService.findUserById(id);

        List<FieldError> errors = userDataValidator.validateUpdateUsername(name);

        if (errors.isEmpty()) {
            userService.updateUsername(user.get(), name);
            attributes.addFlashAttribute("message", "Username successfully updated");
            LOGGER.info("Username updated: {}", name);
        } else {
            attributes.addFlashAttribute("errors", errors);
        }

        return "redirect:/admin/updateuser/" + id;

    }

    @PostMapping("/admin/updateuser/updateuseractive/{id}")
    public String updateUserActive(@PathVariable("id") Long id, @RequestParam("activated") boolean activated, RedirectAttributes attributes) {
        Optional<User> user = userService.findUserById(id);

        if (user.isPresent()) {
            userService.updateUserActivity(user.get(), activated);
            attributes.addFlashAttribute("message", "Role activity updated");
            LOGGER.info("User activity changed: {}", activated);
        }

        return "redirect:/admin/updateuser/" + id;
    }

    @PostMapping("/admin/updateuser/updateuserrole/{id}")
    public String updateUserRole(@PathVariable("id") Long id, @RequestParam(value = "roles", defaultValue = "") List<String> roles,
                                 RedirectAttributes attributes) {
        List<FieldError> errors = userDataValidator.validateUpdateUserRole(roles);

        Optional<User> user = userService.findUserById(id);

        if (errors.isEmpty() && user.isPresent()) {
            attributes.addFlashAttribute("message", "User role successfully updated.");
            userService.updateUserRole(user.get(), roles);
        } else {
            attributes.addFlashAttribute("errors", errors);
            LOGGER.info("User role updated: {}", roles);
        }

        return "redirect:/admin/updateuser/" + id;
    }

    @GetMapping("/admin/createnewuser")
    public String newUser(Model model) {
        model.addAttribute("rolelist", roleService.getAllRoles());

        LOGGER.info("Create new user page displayed.");
        return "createnewuser";
    }

    @PostMapping(value = "/admin/createnewuser")
    public String newUser(RedirectAttributes attributes,
                          @RequestParam(value = "chosenroles", defaultValue = "") List<String> roles, @ModelAttribute UserDto userDto) {

        List<FieldError> errors = userDataValidator.validateNewUserData(userDto.getUsername(), userDto.getPassword(), roles);

        if (errors.isEmpty()) {
            userService.createNewUser(userDto, roles);
            attributes.addFlashAttribute("message", "User created succesfully");
            LOGGER.info("User created succesfully: {}", userDto.getUsername());
        } else {
            attributes.addFlashAttribute("errors", errors);
            LOGGER.info("Invalid inputs: {}", errors.toString());
        }

        return "redirect:/admin/createnewuser";
    }

    @GetMapping("/admin/deleteuser/{id}")
    public String deleteUser(RedirectAttributes attributes, @PathVariable("id") Long id, Principal principal) {
        Optional<User> user = userService.findUserById(id);
        List<FieldError> errors = userDataValidator.validateUserDelete(user, principal.getName());

        if (errors.isEmpty()) {
            attributes.addFlashAttribute("message", "User successfully deleted.");
            userService.deleteUser(user.get());
            LOGGER.info("User deleted with id: {}", id);
        } else {
            attributes.addFlashAttribute("errors", errors);
            LOGGER.info("User not deleted.");
        }

        return "redirect:/admin/userlist";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }

    @GetMapping("/admin/createnewrole")
    public String createNewRole() {
        return "createnewrole";
    }

    @RequestMapping(value = "/admin/createnewrole", method = RequestMethod.POST)
    public String createNewRole(RedirectAttributes attributes, @ModelAttribute RoleDto roleDto) {
        List<FieldError> errors = roleDataValidator.validateNewRole(roleDto.getRole());

        if (errors.isEmpty()) {
            roleService.saveCustomRole(roleDto.getRole());
            attributes.addFlashAttribute("message", "Role created succesfully");
        } else {
            attributes.addFlashAttribute("errors", errors);
        }

        return "redirect:/admin/createnewrole";
    }

    @GetMapping("/admin/rolelist")
    public String updateRoleList(Model model) {
        model.addAttribute("rolelist", roleService.getAllRoles());
        return "rolelist";
    }

    @GetMapping("/admin/deleterole/{id}")
    public String deleteRoleById(RedirectAttributes attributes, @PathVariable("id") Long id) {
        Optional<Role> role = roleService.findRoleById(id);

        List<FieldError> errors = roleDataValidator.validateDeleteRole(role);

        if (errors.isEmpty()) {
            roleService.deleteRoleById(id);
            attributes.addFlashAttribute("message", "Role deleted successfully");
        } else {
            attributes.addFlashAttribute("errors", errors);
        }
        return "redirect:/admin/rolelist/";
    }

    @GetMapping("/admin/updaterole/{id}")
    public String updateRole(Model model, @PathVariable("id") Long id, RedirectAttributes attributes) {
        List<FieldError> errors = roleDataValidator.validateRoleUpdate(roleService.findRoleById(id));

        if (errors.isEmpty()) {
            model.addAttribute("role", roleService.findRoleById(id).get().getRole());
            return "updaterole";
        } else {
            attributes.addFlashAttribute("errors", errors);
            return "redirect:/admin/rolelist/";
        }
    }

    @PostMapping("/admin/updaterole/{id}")
    public String updateRole(Model model, RedirectAttributes attributes, @PathVariable("id") Long id, @RequestParam("rolename") String rolename) {
        Optional<Role> role = roleService.findRoleById(id);

        List<FieldError> errors = roleDataValidator.validateUpdateRole1000(rolename);

        if (errors.isEmpty() && role.isPresent()) {
            roleService.updateRoleName(role.get(), rolename);
            attributes.addFlashAttribute("message", "Rolename updated");
        } else {
            attributes.addFlashAttribute("errors", errors);
        }
        return "redirect:/admin/updaterole/" + id;
    }
}






