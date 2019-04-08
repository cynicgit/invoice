package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/8.
 * @Description:
 */
@Setter
@Getter
public class Credit {
    private Integer id;
    private String creditType;
    private String creditLimit;
}
