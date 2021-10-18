package com.example.qa.order.exchange;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptData {
    private boolean accept;

    @JsonCreator
    public AcceptData(@JsonProperty(required = true, value = "accept") boolean accept) {
        this.accept = accept;
    }
}
