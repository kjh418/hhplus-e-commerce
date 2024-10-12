package hhplus.ecommerce.domain.order;

import hhplus.ecommerce.application.common.OrderPaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderPaymentStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderPaymentStatus status) {
        return status != null ? status.name() : null;
    }

    @Override
    public OrderPaymentStatus convertToEntityAttribute(String data) {
        return data != null ? OrderPaymentStatus.valueOf(data) : null;
    }
}
