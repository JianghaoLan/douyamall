package org.lanjianghao.common.exception;

public class ApplicationException extends RuntimeException {
    private final BizCodeEnum bizCodeEnum;

    public ApplicationException(BizCodeEnum biz) {
        super(biz.getMessage());
        this.bizCodeEnum = biz;
    }

    public BizCodeEnum getBizCodeEnum() {
        return bizCodeEnum;
    }

    public int getCode() {
        return bizCodeEnum.getCode();
    }
}
