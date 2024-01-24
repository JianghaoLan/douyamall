package org.lanjianghao.douyamall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.common.to.OrderTo;
import org.lanjianghao.common.to.SkuHasStockTo;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.order.component.AlipayTemplate;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.entity.OrderItemEntity;
import org.lanjianghao.douyamall.order.entity.PaymentInfoEntity;
import org.lanjianghao.douyamall.order.enume.AlipayTradeStatusEnum;
import org.lanjianghao.douyamall.order.enume.OrderStatusEnum;
import org.lanjianghao.douyamall.order.exception.*;
import org.lanjianghao.douyamall.order.feign.CartFeignService;
import org.lanjianghao.douyamall.order.feign.MemberFeignService;
import org.lanjianghao.douyamall.order.feign.ProductFeignService;
import org.lanjianghao.douyamall.order.feign.WareFeignService;
import org.lanjianghao.douyamall.order.service.OrderItemService;
import org.lanjianghao.douyamall.order.service.PaymentInfoService;
import org.lanjianghao.douyamall.order.to.CreateOrderTo;
import org.lanjianghao.douyamall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.order.dao.OrderDao;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.lanjianghao.douyamall.order.constant.OrderConstant.ORDER_AUTO_CONFIRM_DAY;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    private String generateOrderToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_PREFIX + userId, token,
                OrderConstant.ORDER_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return token;
    }

    private void verifyOrderToken(Long userId, String token) {
        if (userId == null) {
            throw new OrderTokenVerifyFailedException("用户未登录");
        }

        String key = OrderConstant.ORDER_TOKEN_PREFIX + userId;
        final String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) else return 0 end";
        Long ret = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key), token);
        if (ret == null || ret == 0) {
            throw new OrderTokenVerifyFailedException("订单Token验证失败");
        }
    }

    @Override
    public OrderConfirmVo getConfirmOrder(MemberVo loginUser) {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
        if (requestAttributes == null) {
            log.debug("requestAttributes值为空");
        }
//        else {
//            String cookie = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader("Cookie");
//            if (cookie == null) {
//                log.debug("cookie值为空");
//            }
//        }

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(
                () -> {
//                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    orderConfirmVo.setAddresses(memberFeignService.listUserReceiveAddresses());
                }, executor);
        CompletableFuture<Void> getItemsFuture = CompletableFuture.runAsync(
                        () -> {
//                    RequestContextHolder.setRequestAttributes(requestAttributes);
                            orderConfirmVo.setItems(cartFeignService.listUserCartCheckedItems());
                        }, executor)
                .thenRun(() -> {
                    if (orderConfirmVo.getItems() == null) {
                        return;
                    }
                    List<Long> skuIds = orderConfirmVo.getItems().stream()
                            .map(CartItemVo::getSkuId).collect(Collectors.toList());
                    R r = wareFeignService.listHasStocksBySkuIds(skuIds);
                    List<SkuHasStockTo> hasStocks = r.get("data", new TypeReference<List<SkuHasStockTo>>() {
                    });
                    Map<Long, Boolean> hasStockMap = hasStocks.stream()
                            .collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
                    orderConfirmVo.getItems().forEach(
                            item -> item.setHasStock(hasStockMap.getOrDefault(item.getSkuId(), false)));
                });
        CompletableFuture<Void> getIntegrationFuture = CompletableFuture.runAsync(
                () -> {
//                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    orderConfirmVo.setIntegration((Integer) memberFeignService.integration().get("data"));
                }, executor);

        //防重令牌
        CompletableFuture<Void> generateOrderTokenFuture = CompletableFuture.runAsync(
                () -> {
                    String token = generateOrderToken(loginUser.getId());
                    orderConfirmVo.setOrderToken(token);
                }, executor);

        CompletableFuture.allOf(getAddressFuture, getItemsFuture, getIntegrationFuture, generateOrderTokenFuture).join();

        return orderConfirmVo;
    }

    @Transactional
    @Override
    public OrderEntity submitOrder(MemberVo loginUser, SubmitOrderVo submit) {
        verifyOrderToken(loginUser.getId(), submit.getOrderToken());

        CreateOrderTo createOrderTo = createOrderForSecKill(loginUser, submit);

        //验价
        checkPrice(createOrderTo);

        //保存订单和订单项到数据库
        saveOrder(createOrderTo);

        lockStockForOrder(createOrderTo);

        sendCreatedOrderToMQ(createOrderTo.getOrder());

        return createOrderTo.getOrder();
    }

    private void sendCreatedOrderToMQ(OrderEntity order) {
        rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME,
                OrderConstant.MQ_ORDER_CREATE_ORDER_BINDING_ROUTING_KEY, order);
    }

    @Override
    public Integer getOrderStatusByOrderSn(String orderSn) {
        return this.baseMapper.selectStatusByOrderSn(orderSn);
    }

    @Override
    @Transactional
    public void closeOrderByOrderSn(String orderSn) throws CloseOrderFailedException {
        try {
            OrderEntity orderInDb = this.getOrderByOrderSn(orderSn);
            if (!Objects.equals(orderInDb.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
                return;
            }

            //关闭支付宝订单
            alipayTemplate.closeByOrderSn(orderInDb.getOrderSn());

            this.updateStatus(orderInDb.getOrderSn(), OrderStatusEnum.CANCELLED.getCode());

            OrderTo orderForMq = new OrderTo();
            BeanUtils.copyProperties(orderInDb, orderForMq);
            sendClosedOrderToMq(orderForMq);
        } catch (Exception e) {
            throw new CloseOrderFailedException(e);
        }
    }

    @Override
    @Transactional
    public void closeSecKillOrder(CreateSecKillOrderTo createSecKillOrderTo) throws CloseOrderFailedException {
        try {
            OrderEntity orderInDb = this.getOrderByOrderSn(createSecKillOrderTo.getOrderSn());
            if (!Objects.equals(orderInDb.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
                return;
            }

            //关闭支付宝订单
            alipayTemplate.closeByOrderSn(orderInDb.getOrderSn());

            //修改订单状态为取消
            this.updateStatus(orderInDb.getOrderSn(), OrderStatusEnum.CANCELLED.getCode());

            //将取消订单事件发送给mq
            sendReleaseSecKillStockEventToMq(createSecKillOrderTo);
        } catch (Exception e) {
            throw new CloseOrderFailedException(e);
        }
    }

    private void sendReleaseSecKillStockEventToMq(CreateSecKillOrderTo createSecKillOrderTo) {
        rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME,
                OrderConstant.MQ_SEC_KILL_RELEASE_STOCK_BINDING_ROUTING_KEY, createSecKillOrderTo);
    }

    @Override
    public AlipayPayVo getAlipayPayVo(String orderSn) {
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        List<OrderItemEntity> items = orderItemService.listByOrderSn(orderSn);

        AlipayPayVo payVo = new AlipayPayVo();
        payVo.setOutTradeNo(orderSn);
        payVo.setTotalAmount(order.getTotalAmount().setScale(2, RoundingMode.UP).toPlainString());
        payVo.setSubject(items.get(0).getSkuName() + " 等");
        String body = items.stream().map(OrderItemEntity::getSkuName).collect(Collectors.joining("；"));
        payVo.setBody(body);
        return payVo;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public PageUtils queryMemberOrdersPage(Long memberId, Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberId).orderByDesc("create_time")
        );

        Set<String> orderSns = page.getRecords().stream().map(OrderEntity::getOrderSn).collect(Collectors.toSet());
        List<OrderItemEntity> allItems =
                orderItemService.list(new QueryWrapper<OrderItemEntity>().in("order_sn", orderSns));

        IPage<MemberOrderVo> memberOrderPage =
                Page.of(page.getCurrent(), page.getSize(), page.getTotal(), page.searchCount());
        memberOrderPage.setRecords(page.getRecords().stream().map(order -> {
            MemberOrderVo memberOrderVo = new MemberOrderVo();
            memberOrderVo.setOrder(order);
            memberOrderVo.setItems(allItems.stream().filter(
                    item -> item.getOrderSn().equals(order.getOrderSn())).collect(Collectors.toList()));
            return memberOrderVo;
        }).collect(Collectors.toList()));

        return new PageUtils(memberOrderPage);
    }

    @Override
    @Transactional
    public void handlePayment(PaymentInfoEntity payment) {
        try {
            paymentInfoService.save(payment);
        } catch (DataAccessException e) {
            log.error("插入付款记录失败：" + e);
        }

        if (payment.getPaymentStatus().equals(AlipayTradeStatusEnum.TRADE_SUCCESS.toString()) ||
                payment.getPaymentStatus().equals(AlipayTradeStatusEnum.TRADE_FINISHED.toString())) {
            String orderSn = payment.getOrderSn();
            this.updateStatus(orderSn, OrderStatusEnum.PAYED.getCode());
        }
    }

    @Override
    @Transactional
    public void createSecKillOrder(CreateSecKillOrderTo createSecKillOrderTo) {
        CreateOrderTo createOrderTo = createOrderForSecKill(createSecKillOrderTo);

        //保存订单和订单项到数据库
        saveOrder(createOrderTo);

        //秒杀商品已提前锁定库存，无需再锁
//        lockStockForOrder(createOrderTo);
        //发送创建秒杀订单消息到MQ
        sendCreatedSecKillOrderToMQ(createSecKillOrderTo);
    }

    private void sendCreatedSecKillOrderToMQ(CreateSecKillOrderTo createSecKillOrderTo) {
        rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME,
                OrderConstant.MQ_ORDER_SEC_KILL_CREATE_ORDER_BINDING_ROUTING_KEY, createSecKillOrderTo);
    }

    private CreateOrderTo createOrderForSecKill(CreateSecKillOrderTo createSecKillOrderTo) {
        CreateOrderTo to = new CreateOrderTo();

        OrderEntity order = buildOrder(createSecKillOrderTo);
//        order.setReceiveTime();

//        List<OrderItemEntity> orderItems = buildOrderItems(order.getOrderSn());
        OrderItemEntity orderItem = buildOrderItemForSecKill(createSecKillOrderTo);
        List<OrderItemEntity> orderItems = Collections.singletonList(orderItem);
        computePrice(order, orderItems);

        to.setOrder(order);
        to.setOrderItems(orderItems);

        return to;
    }

    private OrderItemEntity buildOrderItemForSecKill(CreateSecKillOrderTo createSecKillOrderTo) {
        OrderItemEntity orderItem = new OrderItemEntity();

        CompletableFuture<Void> getSkuInfoAndSpuInfoFuture = CompletableFuture.supplyAsync(() -> {
            R getSkuInfoR = productFeignService.getSkuInfoBySkuId(createSecKillOrderTo.getSkuId());
            if (getSkuInfoR.getCode() == 0) {
                SkuInfoVo skuInfo = getSkuInfoR.get("skuInfo", SkuInfoVo.class);
                orderItem.setSkuPic(skuInfo.getSkuDefaultImg());
                orderItem.setSkuName(skuInfo.getSkuName());
                orderItem.setSkuPrice(skuInfo.getPrice());          //商品原始价格
                orderItem.setSkuId(skuInfo.getSkuId());
                return skuInfo.getSpuId();
            }
            return null;
        }, executor).thenAccept(spuId -> {
            if (spuId == null) {
                return;
            }
            R getSpuInfoR = productFeignService.getSpuInfoBySpuId(spuId);
            if (getSpuInfoR.getCode() == 0) {
                SpuInfoVo spuInfo = getSpuInfoR.get("spuInfo", SpuInfoVo.class);
                orderItem.setSpuName(spuInfo.getSpuName());
                orderItem.setSpuBrand(spuInfo.getBrandId().toString());
                orderItem.setSpuId(spuInfo.getId());
                orderItem.setCategoryId(spuInfo.getCatalogId());
            }
        });

        CompletableFuture<Void> getSaleAttrFuture = CompletableFuture.runAsync(() -> {
            R getSaleAttrR = productFeignService.getSkuSaleAttrValueStrings(createSecKillOrderTo.getSkuId());
            if (getSaleAttrR.getCode() == 0) {
                List<String> attrValStrings = getSaleAttrR.get("data", new TypeReference<List<String>>() {
                });
                orderItem.setSkuAttrsVals(StringUtils.collectionToDelimitedString(attrValStrings, ";"));
            }
        }, executor);

        orderItem.setOrderSn(createSecKillOrderTo.getOrderSn());
        orderItem.setSkuQuantity(createSecKillOrderTo.getNum());

        BigDecimal total = createSecKillOrderTo.getSeckillPrice().multiply(new BigDecimal(createSecKillOrderTo.getNum()));
        orderItem.setGiftGrowth(total.intValue());
        orderItem.setGiftIntegration(total.intValue());
        orderItem.setPromotionAmount(new BigDecimal(0));
        orderItem.setCouponAmount(new BigDecimal(0));
        orderItem.setIntegrationAmount(new BigDecimal(0));
        //实际金额
        orderItem.setRealAmount(total
                .subtract(orderItem.getPromotionAmount())
                .subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount()));

        CompletableFuture.allOf(getSkuInfoAndSpuInfoFuture, getSaleAttrFuture).join();

        return orderItem;
    }

    private OrderEntity buildOrder(CreateSecKillOrderTo createSecKillOrderTo) {

        OrderEntity order = new OrderEntity();
        order.setMemberId(createSecKillOrderTo.getMemberId());
        order.setOrderSn(createSecKillOrderTo.getOrderSn());

        //暂时没有收获地址信息，需要用户确认页确定
//        order.setFreightAmount(fare.getFare());
//        order.setReceiverCity(fare.getAddress().getCity());
//        order.setReceiverProvince(fare.getAddress().getProvince());
//        order.setReceiverDetailAddress(fare.getAddress().getDetailAddress());
//        order.setReceiverPhone(fare.getAddress().getPhone());
//        order.setReceiverPostCode(fare.getAddress().getPostCode());
//        order.setReceiverRegion(fare.getAddress().getRegion());
//        order.setReceiverName(fare.getAddress().getName());

        order.setDeleteStatus(OrderConstant.OrderDeleteStatusEnum.NOT_DELETED.getCode());
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setAutoConfirmDay(ORDER_AUTO_CONFIRM_DAY);

        return order;
    }

    private void updateStatus(String orderSn, Integer status) {
        this.baseMapper.updateStatus(orderSn, status);
    }

    private void sendClosedOrderToMq(OrderTo order) {
        rabbitTemplate.convertAndSend(OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME,
                OrderConstant.MQ_ORDER_RELEASE_OTHER_BINDING_ROUTING_KEY, order);
    }

    private List<LockStockResultVo> lockStockForOrder(CreateOrderTo createOrderTo) {
        WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
        wareSkuLockVo.setOrderSn(createOrderTo.getOrder().getOrderSn());
        List<LockItemVo> items = createOrderTo.getOrderItems().stream().map(item -> {
            LockItemVo lockItemVo = new LockItemVo();
            lockItemVo.setSkuId(item.getSkuId());
            lockItemVo.setTitle(item.getSkuName());
            lockItemVo.setNum(item.getSkuQuantity());
            return lockItemVo;
        }).collect(Collectors.toList());
        wareSkuLockVo.setItems(items);
        R r = wareFeignService.lockStockForOrder(wareSkuLockVo);
        if (r.getCode() == 0) {
            return r.get("data", new TypeReference<List<LockStockResultVo>>() {
            });
        }
        throw new StockLockFailedException(r.getMsg());
    }

    private void saveOrder(CreateOrderTo createOrderTo) {
        OrderEntity order = createOrderTo.getOrder();
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());

        this.save(order);
        orderItemService.saveBatch(createOrderTo.getOrderItems());
    }

    private void checkPrice(CreateOrderTo createOrderTo) {
        if (createOrderTo.getOrder().getPayAmount().subtract(createOrderTo.getPayPrice()).doubleValue() >= 0.01) {
            throw new OrderPriceCheckFailedException("商品价格有变动，请重新确认");
        }
    }

    private CreateOrderTo createOrderForSecKill(MemberVo loginUser, SubmitOrderVo submit) {
        CreateOrderTo to = new CreateOrderTo();

        OrderEntity order = buildOrder(loginUser.getId(), submit);
//        order.setReceiveTime();

        List<OrderItemEntity> orderItems = buildOrderItems(order.getOrderSn());
        computePrice(order, orderItems);

        to.setOrder(order);
        to.setOrderItems(orderItems);
        to.setPayPrice(submit.getPayPrice());

        return to;
    }

    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
        BigDecimal total = new BigDecimal(0);
        BigDecimal integration = new BigDecimal(0);
        BigDecimal coupon = new BigDecimal(0);
        BigDecimal promotion = new BigDecimal(0);
        int giftGrowth = 0;
        int giftIntegration = 0;
        for (OrderItemEntity item : orderItems) {
            total = total.add(item.getRealAmount());
            integration = integration.add(item.getIntegrationAmount());
            coupon = coupon.add(item.getCouponAmount());
            promotion = promotion.add(item.getPromotionAmount());
            giftGrowth += item.getGiftGrowth();
            giftIntegration += item.getGiftIntegration();
        }
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setIntegrationAmount(integration);
        order.setCouponAmount(coupon);
        order.setPromotionAmount(promotion);
        order.setIntegration(giftIntegration);
        order.setGrowth(giftGrowth);
    }

    private OrderEntity buildOrder(Long loginUserId, SubmitOrderVo submit) {
        OrderEntity order = new OrderEntity();
        order.setMemberId(loginUserId);
        order.setOrderSn(IdWorker.getTimeId());

        R getFareR = wareFeignService.getFare(submit.getAddrId());
        FareVo fare = getFareR.get("data", FareVo.class);
        //设置运费
        order.setFreightAmount(fare.getFare());

        //收货地址信息
        order.setReceiverCity(fare.getAddress().getCity());
        order.setReceiverProvince(fare.getAddress().getProvince());
        order.setReceiverDetailAddress(fare.getAddress().getDetailAddress());
        order.setReceiverPhone(fare.getAddress().getPhone());
        order.setReceiverPostCode(fare.getAddress().getPostCode());
        order.setReceiverRegion(fare.getAddress().getRegion());
        order.setReceiverName(fare.getAddress().getName());

        order.setDeleteStatus(OrderConstant.OrderDeleteStatusEnum.NOT_DELETED.getCode());
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setAutoConfirmDay(ORDER_AUTO_CONFIRM_DAY);

        return order;
    }

    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //获取到的价格为最终价格
        List<CartItemVo> cartItems = cartFeignService.listUserCartCheckedItems();
        if (cartItems == null || cartItems.size() == 0) {
            throw new NoSelectedItemException("购物车中没有选中的商品");
        }

        List<Long> skuIds = cartItems.stream().map(CartItemVo::getSkuId).collect(Collectors.toList());
        R getSpuInfosBySkuIdsR = productFeignService.getSpuInfosBySkuIds(skuIds);
        List<GetSpuInfoBySkuIdVo> spuInfos = getSpuInfosBySkuIdsR.get("data", new TypeReference<List<GetSpuInfoBySkuIdVo>>() {
        });
        Map<Long, SpuInfoVo> spuInfoLookupTable =
                spuInfos.stream().collect(Collectors.toMap(GetSpuInfoBySkuIdVo::getSkuId, GetSpuInfoBySkuIdVo::getSpuInfo));

        return cartItems.stream().map(cartItem -> {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderSn(orderSn);
            item.setSkuId(cartItem.getSkuId());
            item.setSkuPrice(cartItem.getPrice());
            item.setSkuAttrsVals(StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
            item.setSkuName(cartItem.getTitle());
            item.setSkuPic(cartItem.getImage());
            item.setSkuQuantity(cartItem.getCount());

            item.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
            item.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());

            SpuInfoVo spuInfo = spuInfoLookupTable.get(cartItem.getSkuId());
            item.setSpuBrand(spuInfo.getBrandId().toString());
            item.setSpuId(spuInfo.getId());
            item.setSpuName(spuInfo.getSpuName());
            item.setCategoryId(spuInfo.getCatalogId());

            item.setPromotionAmount(new BigDecimal(0));
            item.setCouponAmount(new BigDecimal(0));
            item.setIntegrationAmount(new BigDecimal(0));
            //实际金额
            BigDecimal origPrice = item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity()));
            item.setRealAmount(origPrice
                    .subtract(item.getPromotionAmount())
                    .subtract(item.getCouponAmount())
                    .subtract(item.getIntegrationAmount()));

            return item;
        }).collect(Collectors.toList());
    }
}