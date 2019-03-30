package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportNoReceiver {

    private String key;
    private String keyDesc;
    private Double totalInvoice;
    private Double noReceiveTotalInvoice;
    private Double badInvoice;
}
