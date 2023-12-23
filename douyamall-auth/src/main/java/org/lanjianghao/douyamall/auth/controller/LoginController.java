package org.lanjianghao.douyamall.auth.controller;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.exception.CodeVerificationFailedException;
import org.lanjianghao.douyamall.auth.exception.SmsCodeSendIntervalException;
import org.lanjianghao.douyamall.auth.exception.SmsSendCodeException;
import org.lanjianghao.douyamall.auth.service.LoginService;
import org.lanjianghao.douyamall.auth.service.SmsCodeService;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    SmsCodeService smsCodeService;

    @Autowired
    LoginService loginService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) throws SmsCodeSendIntervalException, SmsSendCodeException {

        smsCodeService.sendSmsCode(phone);

        return R.ok();
    }

    private String handleRegisterValidationError(BindingResult result, RedirectAttributes redirectAttributes) {

        Map<String, String> errors =
                result.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "",
                                (existingValue, newValue) -> existingValue));

        redirectAttributes.addFlashAttribute("errors", errors);
//        model.addAttribute("errors", errors);

        return "redirect:http://auth.douyamall.com/reg.html";
    }

    private String handleCodeVerificationFailed(RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "验证码校验不通过");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.douyamall.com/reg.html";
    }

    private String handleRegisterFailed(R r, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        errors.put("msg", r.getMsg());
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.douyamall.com/reg.html";
    }

    @PostMapping("register")
    public String register(@Validated RegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return handleRegisterValidationError(result, redirectAttributes);
        }

        R r;
        try {
            r = loginService.register(vo);
        } catch (CodeVerificationFailedException e) {
            return handleCodeVerificationFailed(redirectAttributes);
        }
        if (r.getCode() != 0) {
            return handleRegisterFailed(r, redirectAttributes);
        }

        return "redirect:http://auth.douyamall.com/login.html";
    }
}
