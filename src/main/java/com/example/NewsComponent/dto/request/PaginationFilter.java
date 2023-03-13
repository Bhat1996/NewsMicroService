package com.example.NewsComponent.dto.request;

import com.example.NewsComponent.enums.SortingOrder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PaginationFilter {

    @Min(value = 0, message = "Page Offset Can't be less Than 0")
    private Integer offset = 0;

    @Min(value = 1, message = "Page Limit Can't be less Than 1")
    @Max(value = 50, message = "Page Limit Can't be Greater Than 50")
    private Integer limit = 10;

    private SortingOrder order;

    public Integer skip(){
        return offset * limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public SortingOrder getOrder() {
        return order;
    }

    public void setOrder(SortingOrder order) {
        this.order = order;
    }
}
