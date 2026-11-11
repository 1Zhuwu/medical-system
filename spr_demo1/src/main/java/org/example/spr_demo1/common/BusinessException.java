package org.example.spr_demo1.common;

import lombok.Getter;

/**
 * 业务异常：由 Service 层主动抛出，交给 GlobalExceptionHandler 统一转成友好提示。
 * 例如「科室下仍有医生，不能删除」「身份证号已存在」。
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
