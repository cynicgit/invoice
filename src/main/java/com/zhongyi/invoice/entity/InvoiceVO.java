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

    private Double receiveTotalInvoice;
    private Double commonInvoiceAmount;

    private Double commonNoTaxAmount;
    private Double totalNoTaxAmount;
    private Double totalInvoiceAmount;
    private Double totalThisYearNoTaxAmount;
    private Double totalThisYearInvoiceAmount;
    private Double specialInvoiceAmount;
    private Double specialNoTaxAmount;
    private String createLimitPart;
    private String invoiceDateTime;
    private Long startTime;
    private Long endTime;
}
