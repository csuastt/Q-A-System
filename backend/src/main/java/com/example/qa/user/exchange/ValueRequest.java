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
public class ValueRequest {
    Integer value;

    public void checkRechargeOrThrow(int balance) {
        if (!FieldValidator.value(value, 1, SystemConfig.RECHARGE_MAX)) {
            throw new ApiException(403, "RECHARGE_INVALID");
        }
        if (balance + value > SystemConfig.BALANCE_MAX) {
            throw new ApiException(403, "BALANCE_INVALID");
        }
    }
}
