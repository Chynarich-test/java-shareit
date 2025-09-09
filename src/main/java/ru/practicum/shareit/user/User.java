package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.base.BaseEntity;


@Builder
@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
