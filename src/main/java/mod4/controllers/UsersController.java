package mod4.controllers;

import mod4.dao.UserDao;
import mod4.model.Name;
import mod4.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserDao userDao;

    @Autowired
    public UsersController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", userDao.getAll());
        return "users/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("user", userDao.get(id));
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
        userDao.create(newUser);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") UUID id) {
        model.addAttribute("user", userDao.get(id));
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
        userDao.update(id, updUser);
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable("id") UUID id) {
        userDao.remove(id);
        return "redirect:/users";
    }

}
