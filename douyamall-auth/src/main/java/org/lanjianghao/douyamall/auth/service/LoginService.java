package org.lanjianghao.douyamall.auth.service;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.vo.LoginVo;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;

public interface LoginService {
    R register(RegisterVo vo);

    R login(LoginVo vo);
}
