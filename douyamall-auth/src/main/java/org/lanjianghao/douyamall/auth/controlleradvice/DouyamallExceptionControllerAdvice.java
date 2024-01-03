package org.lanjianghao.douyamall.auth.controlleradvice;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理异常
 */
@Slf4j
@RestControllerAdvice("org.lanjianghao.douyamall.auth.controller")
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

    @ExceptionHandler(ApplicationException.class)
    public R handleApplicationException(ApplicationException e) {
        log.error("异常：{}。类型：{}", e.getMessage(), e.getClass().getSimpleName());
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("异常：{}。类型：{}", e.getMessage(), e.getClass().getSimpleName());
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("异常：{}。类型：{}", throwable.getMessage(), throwable.getClass().getSimpleName());
        throwable.printStackTrace();
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMessage());
    }
}
