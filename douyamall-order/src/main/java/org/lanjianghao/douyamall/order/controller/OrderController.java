package org.lanjianghao.douyamall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 订单
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:45:24
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 获取订单状态信息
     */
    @GetMapping("/ordersn/{orderSn}/status")
    public R getOrderStatusByOrderSn(@PathVariable("orderSn") String orderSn) {
        Integer status = orderService.getOrderStatusByOrderSn(orderSn);
        return R.ok().put("data", status);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
