package ro.casabuna.casabuna_backend.model.entity.team;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import ro.casabuna.casabuna_backend.model.entity.BaseEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;

import java.util.Objects;

@Entity
@Table(name = "volunteer_child_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"volunteer_id", "child_id", "team_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerChildAssignmentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "volunteer_child_assignments_seq", sequenceName = "volunteer_child_assignments_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserEntity volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private UserEntity child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VolunteerChildAssignmentEntity that = (VolunteerChildAssignmentEntity) o;
        return Objects.equals(
                volunteer.getId(), that.volunteer.getId()) &&
                Objects.equals(child.getId(), that.child.getId()) &&
                Objects.equals(team.getId(), that.team.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(volunteer.getId(), child.getId(), team.getId());
    }
}
