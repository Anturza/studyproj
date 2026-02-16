package mod4.jpaapi.services;

import mod4.jpaapi.exceptionhandling.exceptions.UserNotFoundException;
import mod4.jpaapi.dto.UserDTO;
import mod4.jpaapi.models.User;
import mod4.jpaapi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public static UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthday()
        );
    }

}
