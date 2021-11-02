package com.example.qa.user.exchange;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.utils.FieldValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplyRequest {
    String description;
    Integer price;

    public void validateOrThrow() {
        if (!FieldValidator.length(description, SystemConfig.DESCRIPTION_MIN_LENGTH, SystemConfig.DESCRIPTION_MAX_LENGTH)) {
            throw new ApiException(403, "DESCRIPTION_INVALID");
        }
        if (!FieldValidator.value(price, SystemConfig.getMinPrice(), SystemConfig.getMaxPrice())) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }
}
