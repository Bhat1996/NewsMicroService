package com.example.NewsComponent.dto.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    private String countryID;
    private String stateID;
    private String districtID;
    private String tehsilID;
    private String villageID;
    private String lang;
}
