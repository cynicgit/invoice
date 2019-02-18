package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Target {

    private Integer id;

    private String name;

    private Double target;

    private Integer type;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date year;

    private Integer userId;

    private Integer groupId;

}
