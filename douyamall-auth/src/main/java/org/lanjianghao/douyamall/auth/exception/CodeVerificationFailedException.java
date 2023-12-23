package org.lanjianghao.douyamall.auth.exception;

public class CodeVerificationFailedException extends RuntimeException {
    public CodeVerificationFailedException(String msg) {
        super(msg);
    }
}
