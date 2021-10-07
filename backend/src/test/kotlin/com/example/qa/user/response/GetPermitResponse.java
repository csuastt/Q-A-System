package com.example.qa.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetPermitResponse {

    @JsonProperty("permit")
    private String permit;
}
