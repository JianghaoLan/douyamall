package org.lanjianghao.douyamall.member.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.member.feign.OrderFeignService;
import org.lanjianghao.douyamall.member.vo.MemberOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberWebController {
    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping({"/orderList.html", "/orderlist.html"})
    public String orderListPage(@RequestParam(value = "pageNum", defaultValue = "1") Long pageNum, Model model) {
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("page", pageNum);
        R r = orderFeignService.listMemberOrders(pageParam);
        PageUtils page = r.get("page", PageUtils.class);
        List<MemberOrderVo> orders = mapper.convertValue(page.getList(), new TypeReference<List<MemberOrderVo>>() {});

        model.addAttribute("orders", orders);
        model.addAttribute("page", page);
        return "orderList";
    }
}
