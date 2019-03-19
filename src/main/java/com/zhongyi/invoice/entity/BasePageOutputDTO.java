package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BasePageOutputDTO {


    protected long total;
    protected int page;
    protected boolean hasNext;
    protected List list;



}