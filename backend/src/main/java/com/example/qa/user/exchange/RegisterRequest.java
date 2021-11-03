package com.example.qa.user.exchange;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.utils.FieldValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.EmailValidator;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public void validateOrThrow() {
        if (!FieldValidator.length(username, SystemConfig.USERNAME_MIN_LENGTH, SystemConfig.USERNAME_MAX_LENGTH)
                || username.contains("@")) {
            throw new ApiException(403, "USERNAME_INVALID");
        }
        if (!FieldValidator.length(password, SystemConfig.PASSWORD_MIN_LENGTH, SystemConfig.PASSWORD_MAX_LENGTH)) {
            throw new ApiException(403, "PASSWORD_INVALID");
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new ApiException(403, "EMAIL_INVALID");
        }
    }
}
