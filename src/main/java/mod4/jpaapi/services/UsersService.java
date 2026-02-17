package mod4.jpaapi.services;

import mod4.jpaapi.exceptionhandling.exceptions.NotValidUserInputException;
import mod4.jpaapi.exceptionhandling.exceptions.UserNotFoundException;
import mod4.jpaapi.dto.UserDTO;
import mod4.jpaapi.models.Name;
import mod4.jpaapi.models.User;
import mod4.jpaapi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public UserDTO getUser(UUID id) {
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> lst = usersRepository.findAll();
        return lst.stream().map(UsersService::mapToDTO).toList();
    }

    public ResponseEntity<UserDTO> createUser(User user) {
        checkReceivedUserData(user);
        User newUser = mapReceivedUser(user, new User());
        User createdUser = usersRepository.save(newUser);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getId()).toUri();
        UserDTO createdUserDto = UsersService.mapToDTO(createdUser);
        return ResponseEntity.created(location).body(createdUserDto);
    }

    public ResponseEntity<UserDTO> updateUser(UUID id, User userDetails) {
        Optional<User> userOpt = usersRepository.findById(id);
        checkReceivedUserData(userDetails);
        if (userOpt.isPresent()) {
            User user = mapReceivedUser(userDetails, userOpt.get());
            user.setUpdated(LocalDateTime.now());
            User updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(UsersService.mapToDTO(updatedUser));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteUser(UUID id) {
        if (usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthday()
        );
    }

    public static User mapReceivedUser(User userDetails, User targetUser) {
        Name userName = Name.nameFromString(userDetails.getName().toString());
        targetUser.setName(userName);
        targetUser.setEmail(userDetails.getEmail());
        targetUser.setBirthday(userDetails.getBirthday());
        targetUser.setCreated(LocalDateTime.now());
        targetUser.setUpdated(LocalDateTime.now());
        return targetUser;
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
