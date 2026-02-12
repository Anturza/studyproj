package mod4.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    protected UUID id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "name_id", referencedColumnName = "id")
    @NotEmpty(message = "Name can not be empty")
    @Size(min = 1, max = 45, message = "Name length should be between 1 and 45 characters")
    protected Name name;

    @Column(length = 85)
    @Email(message = "email should be valid", regexp = "^$|^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")
    protected String email;

    @Column(name = "user_age", nullable = false)
    @Min(value = 0, message = "can not be less than 0")
    @Max(value = 150, message = "can not be more than 150")
    @Digits(message = "only integer digits acceptable", integer = 3, fraction = 0)
    protected int age;

    @Column(name = "created_at", updatable = false)
    protected LocalDateTime created;
    //TODO: better have builder
    public User() {
    }

    public User(UUID id, Name name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;

    }

    public User(Name name, String email, int age, LocalDateTime created) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.created = created;
    }

    public User(UUID id, Name name, String email, int age, LocalDateTime created) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.created = created;
    }

    public UUID getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age && Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age);
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, name=%s, email=%s, age=%d, created=%s}", id, name, email, age, created);
    }
}


