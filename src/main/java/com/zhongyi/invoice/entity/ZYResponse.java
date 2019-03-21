package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZYResponse {

    private Integer result;

    private Object data;

    public static ZYResponse success() {
        return new ZYResponse(0, null);
    }
    public static ZYResponse success(Object data) {
        return new ZYResponse(0, data);
    }


     public static ZYResponse error() {
        return new ZYResponse(1, null);
    }
    public static ZYResponse error(Object data) {
        return new ZYResponse(1, data);
    }

    public static ZYResponse notLogin() {
        return new ZYResponse(-1, null);

    }
}
