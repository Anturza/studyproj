package mod4.controllers;

import mod4.dao.UserDao;
import mod4.model.Name;
import mod4.model.User;
import mod4.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersControllerJPA {

    private final UsersService usersService;

    @Autowired
    public UsersControllerJPA(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", usersService.findAll());
        return "users/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("user", usersService.findById(id));
        return "users/show";
    }

    @GetMapping("/new")
    public String newPerson(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping()
    public String create(@RequestParam("name") String name,
                         @RequestParam("birthday")LocalDate birthday,
                         @RequestParam("email") String email) {
        Name parsedName = Name.nameFromString(name);
        User newUser = new User();
        newUser.setName(parsedName);
        newUser.setBirthday(birthday);
        newUser.setEmail(email);
        newUser.setCreated(LocalDateTime.now());
        usersService.save(newUser);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") UUID id) {
        model.addAttribute("user", usersService.findById(id));
        return "/users/edit";
    }

    @PatchMapping("/{id}")
    public String Update(@RequestParam("name") Name name,
                         @RequestParam("birthday") LocalDate birthday,
                         @RequestParam("email") String email,
                         @PathVariable("id") UUID id) {
        User updUser = new User();
        updUser.setName(name);
        updUser.setBirthday(birthday);
        updUser.setEmail(email);
        usersService.save(id, updUser);
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable("id") UUID id) {
        usersService.deleteById(id);
        return "redirect:/users";
    }

}
