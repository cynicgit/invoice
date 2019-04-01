package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/1.
 * @Description:
 */
@Getter
@Setter
public class TaxRate {

    private Integer id;

    @Pattern(regexp = "/^100$|^(\\d|[1-9]\\d)$",message = "请输入0-100的整数")
    private Integer taxRate;
}
