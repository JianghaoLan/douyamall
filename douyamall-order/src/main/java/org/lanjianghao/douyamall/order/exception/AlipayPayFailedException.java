package org.lanjianghao.douyamall.order.exception;

public class AlipayPayFailedException extends RuntimeException {
    public AlipayPayFailedException(String msg) {
        super(msg);
    }

    public AlipayPayFailedException(Throwable e) {
        super(e);
    }
}
