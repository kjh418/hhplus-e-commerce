package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.product.PopularProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularProductRepository extends JpaRepository<PopularProduct, Long> {
}
