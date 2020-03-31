package com.companyname.parking.api.domain.parkingspot;

import com.companyname.parking.api.domain.audit.AbstractAuditingEntity;
import com.companyname.parking.api.domain.user.User;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "parking_spot")
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParkingSpot extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "is_free", nullable = false)
    boolean isFree;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owned_account_id", referencedColumnName = "id", nullable = false)
    private User ownedAccount;

    public ParkingSpot freeUp() {
        return this.setFree(true);
    }

    public ParkingSpot hold() {
        return this.setFree(false);
    }

}
