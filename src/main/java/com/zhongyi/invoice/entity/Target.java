package com.zhongyi.invoice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String year;

    private Integer userId;

    private Integer groupId;

}
