package org.lanjianghao.douyamall.product.controlleradvice;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理异常
 */
@Slf4j
@RestControllerAdvice("org.lanjianghao.douyamall.product.controller")
public class DouyamallExceptionControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(
                (fieldError) -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        log.error("数据校验不通过：{}， 异常类型：{}", e.getMessage(), e.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMessage())
                .put("data", errorMap);
    }

    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable throwable) {
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMessage());
    }
}
