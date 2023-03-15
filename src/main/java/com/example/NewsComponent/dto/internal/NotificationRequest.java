package com.example.NewsComponent.dto.internal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotificationRequest {
    private String action;
    private String priority;
    private Filter filter;
    private Messages messages;
    private String value;
    private String requestID;
    private List<String> translations;
    private String postedBy;
    private String userImage;



}
