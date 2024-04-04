package com.leyuna.base.iservice;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.io.Serializable;
import java.util.List;

/**
 * :)
 *
 * @author LeYunone
 * @email 365627310@qq.com
 * @date 2024/4/6
 */
public interface IOperationService<DO> {


    /**
     * 插入或更新 ： 带id更新  反之插入
     * @return
     */
    boolean insertOrUpdate(Object entity);

    /**
     * 批量插入
     * @param params  会通过copy转换为实体
     * @return
     */
    boolean insertOrUpdateBatch(List params);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    boolean deleteById(Serializable id);

    /**
     * 根据ids批量删除
     * @param ids
     * @return
     */
    boolean deleteByIds(List ids);

    <R> boolean deleteLogicById(Serializable id, SFunction<DO, R> tableId);
}
