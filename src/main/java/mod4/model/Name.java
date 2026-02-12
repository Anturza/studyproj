package mod4.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "usernames")
public class Name {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "names_id_gen")
    @SequenceGenerator(name = "names_id_gen", sequenceName = "names_id_seq", allocationSize = 1)
    private long id;
    @Column(length = 50)
    private String surname;
    @Column(length = 50, nullable = false)
    private String personalName;
    @Column(length = 50)
    private String patronymic;

    public Name() {}

    public Name(String personalName) {
        this.personalName = personalName;
    }

    public Name(String surname, String personalName) {
        this.personalName = personalName;
        this.surname = surname;
    }

    public Name(String surname, String personalName, String patronymic) {
        this.surname = surname;
        this.personalName = personalName;
        this.patronymic = patronymic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Name name = (Name) o;
        return Objects.equals(surname, name.surname) && Objects.equals(personalName, name.personalName) && Objects
                .equals(patronymic, name.patronymic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname, personalName, patronymic);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        if (surname != null) joiner.add(surname);
        if (personalName != null) joiner.add(personalName);
        if (patronymic != null) joiner.add(patronymic);
        return joiner.toString();
    }
}
