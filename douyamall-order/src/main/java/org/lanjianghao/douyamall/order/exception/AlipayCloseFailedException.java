package org.lanjianghao.douyamall.order.exception;

public class AlipayCloseFailedException extends Exception {
    public AlipayCloseFailedException(Throwable e) {
        super(e);
    }

    public AlipayCloseFailedException() { }
}
