package ro.casabuna.casabuna_backend.model.entity.volunteer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ro.casabuna.casabuna_backend.model.entity.BaseEntity;
import ro.casabuna.casabuna_backend.model.entity.UserEntity;
import ro.casabuna.casabuna_backend.model.dto.volunteer.VolunteerRequestStatus;

import java.util.List;

@Entity
@Table(name = "volunteer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "volunteer_requests_seq", sequenceName = "volunteer_requests_seq", allocationSize = 1)
    private Long id;

    @Column(name = "self_description")
    private String selfDescription;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "phone")
    private String phone;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name="education_activities", columnDefinition = "text[]")
    private List<String> educationActivities;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "education_level", columnDefinition = "text[]")
    private List<String> educationLevels;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "health_activities", columnDefinition = "text[]")
    private List<String> healthActivities;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "it_support_activities", columnDefinition = "text[]")
    private List<String> itSupportActivities;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "logistic_activities", columnDefinition = "text[]")
    private List<String> logisticActivities;

    @Column(name = "other_activities")
    private String otherActivities;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VolunteerRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_user_id")
    private UserEntity requestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_user_id")
    private UserEntity approver;
}
