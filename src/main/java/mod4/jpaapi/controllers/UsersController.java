package mod4.jpaapi.controllers;

import mod4.jpaapi.exceptionhandling.exceptions.NotValidUserInputException;
import mod4.jpaapi.dto.UserDTO;
import mod4.jpaapi.models.Name;
import mod4.jpaapi.models.User;
import mod4.jpaapi.repositories.UsersRepository;
import mod4.jpaapi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersRepository usersRepository;

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersRepository usersRepository, UsersService usersService) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @Autowired


    @GetMapping
    public List<UserDTO> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = usersService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody User user) {
        checkReceivedUserData(user);
        User newUser = new User();
        Name userName = Name.nameFromString(user.getName().toString());
        newUser.setName(userName);
        newUser.setEmail(user.getEmail());
        newUser.setBirthday(user.getBirthday());
        newUser.setCreated(LocalDateTime.now());
        newUser.setUpdated(LocalDateTime.now());
        usersRepository.save(newUser);
        return UsersService.mapToDTO(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        Optional<User> userOpt = usersRepository.findById(id);
        checkReceivedUserData(userDetails);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Name userName = Name.nameFromString(userDetails.getName().toString());
            user.setName(userName);
            user.setEmail(userDetails.getEmail());
            user.setBirthday(userDetails.getBirthday());
            user.setCreated(userDetails.getCreated());
            user.setUpdated(LocalDateTime.now());
            User updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(UsersService.mapToDTO(updatedUser));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        if (usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static void checkReceivedUserData(User userDetails) {
        if (userDetails.getName() == null || userDetails.getName().toString().isBlank()){
            throw new NotValidUserInputException("Username can not be empty and must not be over 100 characters");
        }
        if (!userDetails.getEmail().matches("^$|^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")) {
            throw new NotValidUserInputException("Provided not valid email");
        }
        if (userDetails.getBirthday().isAfter(LocalDate.now())) {
            throw new NotValidUserInputException("Day of birth can not be more than current date");
        }
    }

}
