package hhplus.ecommerce.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String address;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}", message = "전화번호 형식은 XXX-XXX-XXXX이어야 합니다.")
    private String phoneNumber;

    private LocalDateTime createdAt;

    protected Users() {}
}
