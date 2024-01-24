package org.lanjianghao.douyamall.seckill.exception;

public class SecKillSkuNotExistsException extends SecKillFailedException {
    public SecKillSkuNotExistsException() {
        super("秒杀商品不存在");
    }

    public SecKillSkuNotExistsException(String msg) {
        super(msg);
    }
}
