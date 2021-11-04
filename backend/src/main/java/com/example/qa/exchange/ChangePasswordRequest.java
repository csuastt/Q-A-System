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
public class ChangePasswordRequest {
    private String original;
    private String password;

    public void validatePasswordOrThrow() {
        if (!FieldValidator.length
                (password, SystemConfig.PASSWORD_MIN_LENGTH, SystemConfig.PASSWORD_MAX_LENGTH)) {
            throw new ApiException(403, "PASSWORD_INVALID");
        }
    }
}
