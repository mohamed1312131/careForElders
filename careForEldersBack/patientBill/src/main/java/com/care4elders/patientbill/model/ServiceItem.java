// src/main/java/com/care4elders/patientbill/model/ServiceItem.java
package com.care4elders.patientbill.model;

import lombok.Data;


import org.springframework.data.annotation.*;;;


@Data
public class ServiceItem {
    @Id
    private String id;
    private String serviceName;
    private String description;
    private double rate;
    private int quantity;
    


}