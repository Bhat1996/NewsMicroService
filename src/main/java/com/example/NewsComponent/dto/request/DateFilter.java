package com.example.NewsComponent.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DateFilter {
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
}
