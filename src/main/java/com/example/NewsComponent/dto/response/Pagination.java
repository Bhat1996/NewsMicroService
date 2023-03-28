package com.example.NewsComponent.dto.response;

import java.util.List;

public class Pagination<T> {

    private final List<T> list;
    private final PageInfo pageInfo;

    public Pagination(List<T> list, PageInfo pageInfo) {
        this.list = list;
        this.pageInfo = pageInfo;
    }
    public List<T> getList() {
        return list;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}
