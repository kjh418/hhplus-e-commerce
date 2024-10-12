package hhplus.ecommerce.domain.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OptionConverter implements AttributeConverter<Option, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Option option) {
        try {
            return objectMapper.writeValueAsString(option);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Option convertToEntityAttribute(String data) {
        try {
            return objectMapper.readValue(data, Option.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}