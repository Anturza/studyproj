package mod4.jpaapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class UserBaseModel {

    @Id
    @GeneratedValue
    @UuidGenerator
    protected UUID id;


    @Embedded
    protected Name name;

    @Column(length = 85)
    protected String email;

    @Column(name = "birthday", nullable = false)
    protected LocalDate birthday;

    @Column(name = "created_at", updatable = false)
    protected LocalDateTime created;

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updated;

}
