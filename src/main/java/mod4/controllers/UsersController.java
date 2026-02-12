package mod4.controllers;

import mod4.dao.UserDao;
import mod4.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.validation.Valid;
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

    @GetMapping("new")
    public String newPerson(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User user, BindingResult validationResult) {
        if (validationResult.hasErrors()){
            return "users/new";
        }
        userDao.create(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") UUID id) {
        model.addAttribute("user", userDao.get(id));
        return "/users/edit";
    }

    @PatchMapping("/{id}")
    public String Update(@ModelAttribute("user") @Valid User user, BindingResult validationResult,
                         @PathVariable("id") UUID id) {
        if (validationResult.hasErrors()) {
            return "users/edit";
        }
        userDao.update(id, user);
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable("id") UUID id) {
        userDao.remove(id);
        return "redirect:/users";
    }

}
