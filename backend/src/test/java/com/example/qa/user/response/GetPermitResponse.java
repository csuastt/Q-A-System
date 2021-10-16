package com.example.qa.user.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetPermitResponse {

    @SerializedName("permit")
    private String permit;
}
