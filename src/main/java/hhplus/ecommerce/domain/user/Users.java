package hhplus.ecommerce.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @Column(length = 45)
    private String phoneNumber;

    private LocalDateTime createdAt;

    protected Users() {}
}
