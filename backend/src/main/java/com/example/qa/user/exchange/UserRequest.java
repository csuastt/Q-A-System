package com.example.qa.user.exchange;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.model.User;
import com.example.qa.utils.FieldValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    private String nickname;
    private String email;
    private String phone;
    private User.Gender gender;
    private Integer price;
    private String description;
    private User.Role role;
    private Integer balance;

    public void validateOrThrow() {
        if (!FieldValidator.lengthIfNotNull(nickname, SystemConfig.NICKNAME_MIN_LENGTH, SystemConfig.NICKNAME_MAX_LENGTH)) {
            throw new ApiException(403, "NICKNAME_INVALID");
        }
        if (!FieldValidator.lengthIfNotNull(description, SystemConfig.DESCRIPTION_MIN_LENGTH, SystemConfig.DESCRIPTION_MAX_LENGTH)) {
            throw new ApiException(403, "DESCRIPTION_INVALID");
        }
        if (!FieldValidator.valueIfNotNull(price, SystemConfig.getMinPrice(), SystemConfig.getMaxPrice())) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }
}
