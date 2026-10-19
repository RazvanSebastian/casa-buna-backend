package ro.casabuna.casabuna_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "job_details")
    private String jobDetails;

    @Column(name = "age")
    private int age;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private UserEntity user;
}
