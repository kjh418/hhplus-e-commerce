package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
