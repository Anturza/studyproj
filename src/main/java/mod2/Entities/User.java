package mod2.Entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "Users")
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    protected long id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "name_id", referencedColumnName = "id")
    protected Name name;
    @Column(length = 85)
    protected String email;
    @Column(name = "user_age", nullable = false)
    protected int age;
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime created;

    public User(Name name, String email, int age, LocalDateTime created) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.created = created;
    }

    public long getId() {
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
}


