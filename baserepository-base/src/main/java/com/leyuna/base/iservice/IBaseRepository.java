package com.leyuna.base.iservice;

import java.util.List;

/**
 * @author pengli
 * @create 2022-03-28 16:54
 * 基础服务接口 T规定  入参
 */
public interface IBaseRepository<T> extends IQueryService<T> {

    /**
     * 插入或更新 ： 带id更新  反之插入
     * @param o
     * @return
     */
    T insertOrUpdate(Object o);

    /**
     * 批量插入
     * @param params  会通过copy转换为实体
     * @return
     */
    boolean batchCreate(List params);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int delete(Object id);

    /**
     * 根据ids批量删除
     * @param ids
     * @return
     */
    int batchDelete(List ids);
}
