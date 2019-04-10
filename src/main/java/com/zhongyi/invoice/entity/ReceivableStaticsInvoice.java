package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivableStaticsInvoice extends Invoice {

    private Double limitAmount0;
    private Double limitAmount1;
    private Double limitAmount2;
    private Double limitAmount3;
    private Double limitAmount4;
    private Double limitAmount5;
    private Double limitAmount6;

}
