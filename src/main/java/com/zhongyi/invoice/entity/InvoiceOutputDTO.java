package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/12.
 * @Description:
 */
@Setter
@Getter
public class InvoiceOutputDTO extends BasePageOutputDTO {
    private List<InvoiceVO> list;
}
