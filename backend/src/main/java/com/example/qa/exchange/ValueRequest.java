package com.example.qa.exchange;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.utils.FieldValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValueRequest {
    Integer value;
    String text;

    public void checkRechargeOrThrow(int balance) {
        if (!FieldValidator.value(value, 1, SystemConfig.RECHARGE_MAX)) {
            throw new ApiException(403, "RECHARGE_INVALID");
        }
        if (balance + value > SystemConfig.BALANCE_MAX) {
            throw new ApiException(403, "BALANCE_INVALID");
        }
    }

    public void checkRatingOrThrow() {
        if (!FieldValidator.value(value, 1, 5)) {
            throw new ApiException(403, "RATING_INVALID");
        }
        if (!FieldValidator.lengthIfNotNull(text, 0, 200)) {
            throw new ApiException(403, "TEXT_INVALID");
        }
    }
}
