package com.zhongyi.invoice.entity;

import lombok.Data;

@Data
public class InvoiceVO extends Invoice {

    private String startDate;

    private String endDate;

    private Double totalInvoice;
    private Double sumTotalInvoice;

    private Double noReceiveTotalInvoice;
    private Double sumNoReceiveTotalInvoice;

    private Integer Invoice;
    private Double receiveTotalInvoice;
    private Double commonInvoiceAmount;
    private Double commonNoTaxAmount;
    private Double specialInvoiceAmount;
    private Double specialNoTaxAmount;
    private String createLimitPart;

}
