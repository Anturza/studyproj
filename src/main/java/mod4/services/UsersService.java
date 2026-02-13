package mod4.services;

import mod4.model.User;
import mod4.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public List<User> findAll() {
        return usersRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return usersRepository.findById(id);
    }

    public void save(User user) {
        usersRepository.save(user);
    }

    public void deleteById(UUID id) {
        usersRepository.deleteById(id);
    }

}
