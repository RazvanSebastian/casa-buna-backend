package ro.casabuna.casabuna_backend.model.entity.team;

import jakarta.persistence.*;
import lombok.*;
import ro.casabuna.casabuna_backend.model.entity.BaseEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;

import java.util.Set;

@Entity
@Table(name = "teams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TeamEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "teams")
    private Set<UserEntity> users;
}
