package mod4.jpaapi.dto;

import mod4.jpaapi.models.Name;

import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(
        UUID id,
        Name name,
        String email,
        LocalDate birthday
) {}
