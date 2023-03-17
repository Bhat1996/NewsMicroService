package com.example.NewsComponent.dto.response;

import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.enums.SortingOrder;

public class PageInfo {
    private Integer offset;

    private Boolean hasNext;

    private Boolean hasPrevious;

    private Long totalCounts;

    private final Integer limit;

    private Integer pageNumber;

    private final SortingOrder order;

    public PageInfo(Integer offset, Integer limit, SortingOrder order) {
        this.offset = offset;
        this.limit = limit;
        this.order = order;
    }

    public PageInfo( Long totalCounts, Integer offset, Integer limit, SortingOrder order) {
        this.offset = offset;
        this.totalCounts = totalCounts;
        this.limit = limit;
        this.order = order;

        setPaginationDetails();
    }

    public static PageInfo ofInputs(final Integer offset, final Integer limit, final SortingOrder order)
    {
        return new PageInfo(offset, limit, order);
    }

    public static PageInfo ofResult(final Long totalCounts, final PageInfo pageInput)
    {
        return new PageInfo(pageInput.offset, pageInput.limit,  pageInput.order);
    }

    public static PageInfo ofResult(final Long totalCounts,final PaginationFilter pageInput) {
        return new PageInfo(totalCounts, pageInput.getOffset(), pageInput.getLimit(), pageInput.getOrder());
    }

    private void setPaginationDetails()
    {
        pageNumber = offset + 1;
        hasPrevious  = pageNumber != 1;

        hasNext = (totalCounts - ((long)pageNumber * limit) > 0);
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer skip()
    {
        return offset * limit;
    }

    public SortingOrder getOrder() {
        return order;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "offset=" + offset +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", totalCounts=" + totalCounts +
                ", limit=" + limit +
                ", pageNumber=" + pageNumber +
                ", order=" + order +
                '}';
    }
}
