package com.example.NewsComponent.dto.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter {
    private String phone;
    private String app;
    private Location location;
    private Interests interests;

}
