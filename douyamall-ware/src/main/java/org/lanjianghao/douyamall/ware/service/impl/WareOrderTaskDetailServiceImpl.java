package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.constant.WareConstant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.WareOrderTaskDetailDao;
import org.lanjianghao.douyamall.ware.entity.WareOrderTaskDetailEntity;
import org.lanjianghao.douyamall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> listLockedEntitiesByTaskId(Long taskId) {
        return this.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", taskId)
                .eq("lock_status", WareConstant.OrderTaskLockStatusEnum.LOCKED.getCode()));
    }

}