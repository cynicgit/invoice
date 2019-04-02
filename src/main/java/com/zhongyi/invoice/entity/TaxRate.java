package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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

    @NotNull(message = "请输入税率")
    private Integer taxRate;

}
