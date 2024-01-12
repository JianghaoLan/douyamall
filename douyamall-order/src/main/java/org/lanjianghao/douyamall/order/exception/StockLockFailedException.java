package org.lanjianghao.douyamall.order.exception;

public class StockLockFailedException extends RuntimeException {
    public StockLockFailedException(String msg) { super(msg); }
}
