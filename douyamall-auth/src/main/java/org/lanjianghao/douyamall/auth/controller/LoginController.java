package org.lanjianghao.douyamall.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.exception.CodeVerificationFailedException;
import org.lanjianghao.douyamall.auth.exception.SmsCodeSendIntervalException;
import org.lanjianghao.douyamall.auth.exception.SmsSendCodeException;
import org.lanjianghao.douyamall.auth.service.LoginService;
import org.lanjianghao.douyamall.auth.service.SmsCodeService;
import org.lanjianghao.douyamall.auth.vo.LoginVo;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

    @PostMapping("login")
    public String login(LoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R r = loginService.login(vo);
        if (r.getCode() != 0) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getMsg());
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.douyamall.com/login.html";
        }

        MemberVo member = r.get("data", MemberVo.class);
        session.setAttribute(AuthConstant.LOGIN_USER_SESSION_KEY, member);
        log.debug("登录成功。用户信息：{}", member.toString());

        return "redirect:http://douyamall.com";
    }

    @RequestMapping("login.html")
    public String loginPage(HttpSession session) {
        MemberVo member = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER_SESSION_KEY);
        if (member != null) {
            return "redirect:http://douyamall.com";
        }

        return "login";
    }
}
