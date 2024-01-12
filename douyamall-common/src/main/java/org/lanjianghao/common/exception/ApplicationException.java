package org.lanjianghao.common.exception;

public class ApplicationException extends RuntimeException {
//    private final BizCodeEnum bizCodeEnum;
    private final int code;

    public ApplicationException(BizCodeEnum biz) {
        super(biz.getMessage());
        this.code = biz.getCode();
    }

    public ApplicationException(BizCodeEnum biz, String message) {
        super(message);
        this.code = biz.getCode();
    }

    public ApplicationException(int code, String msg) {
        super(msg);
        this.code = code;
    }

//    public BizCodeEnum getBizCodeEnum() {
//        return bizCodeEnum;
//    }

    public int getCode() {
        return code;
    }
}
