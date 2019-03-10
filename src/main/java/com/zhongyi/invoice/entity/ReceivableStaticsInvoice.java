package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivableStaticsInvoice extends Invoice {

    private String limitAmount1;
    private String limitAmount2;
    private String limitAmount3;
    private String limitAmount4;

}
