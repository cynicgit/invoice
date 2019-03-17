package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BasePageOutputDTO {


    protected long total;
    protected int page;
    protected boolean hasNext;



}