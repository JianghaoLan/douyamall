package org.lanjianghao.douyamall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.lanjianghao.common.to.OrderTo;
import org.lanjianghao.common.to.SkuHasStockTo;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.entity.OrderItemEntity;
import org.lanjianghao.douyamall.order.enume.OrderStatusEnum;
import org.lanjianghao.douyamall.order.exception.NoSelectedItemException;
import org.lanjianghao.douyamall.order.exception.OrderPriceCheckFailedException;
import org.lanjianghao.douyamall.order.exception.OrderTokenVerifyFailedException;
import org.lanjianghao.douyamall.order.exception.StockLockFailedException;
import org.lanjianghao.douyamall.order.feign.CartFeignService;
import org.lanjianghao.douyamall.order.feign.MemberFeignService;
import org.lanjianghao.douyamall.order.feign.ProductFeignService;
import org.lanjianghao.douyamall.order.feign.WareFeignService;
import org.lanjianghao.douyamall.order.service.OrderItemService;
import org.lanjianghao.douyamall.order.to.CreateOrderTo;
import org.lanjianghao.douyamall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                    List<SkuHasStockTo> hasStocks = r.get("data", new TypeReference<List<SkuHasStockTo>>() {});
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

        CreateOrderTo createOrderTo = createOrder(loginUser, submit);

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
    public void closeOrder(OrderEntity order) {
        OrderEntity orderInDb = this.getById(order.getId());
        if (!Objects.equals(orderInDb.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            return;
        }

        OrderEntity entForUpdate = new OrderEntity();
        entForUpdate.setId(orderInDb.getId());
        entForUpdate.setStatus(OrderStatusEnum.CANCELLED.getCode());
//        entForUpdate.setDeleteStatus(OrderConstant.OrderDeleteStatusEnum.DELETED.getCode());
        this.updateById(entForUpdate);

        OrderTo orderForMq = new OrderTo();
        BeanUtils.copyProperties(order, orderForMq);
        sendClosedOrderToMq(orderForMq);
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

    private CreateOrderTo createOrder(MemberVo loginUser, SubmitOrderVo submit) {
        CreateOrderTo to = new CreateOrderTo();

        OrderEntity order = buildOrder(loginUser, submit);
//        order.setReceiveTime();

        List<OrderItemEntity> orderItems = buildOrderItems(order.getOrderSn());
        computePrice(order, orderItems);

        order.setDeleteStatus(OrderConstant.OrderDeleteStatusEnum.NOT_DELETED.getCode());
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setAutoConfirmDay(ORDER_AUTO_CONFIRM_DAY);

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
        order.setPayAmount(total.add(order.getFreightAmount()));
        order.setIntegrationAmount(integration);
        order.setCouponAmount(coupon);
        order.setPromotionAmount(promotion);
        order.setIntegration(giftIntegration);
        order.setGrowth(giftGrowth);
    }

    private OrderEntity buildOrder(MemberVo loginUser, SubmitOrderVo submit) {
        OrderEntity order = new OrderEntity();
        order.setMemberId(loginUser.getId());
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