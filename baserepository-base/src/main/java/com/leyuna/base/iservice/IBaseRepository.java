package com.leyuna.base.iservice;

import java.util.List;

/**
 * @author pengli
 * @create 2022-03-28 16:54
 * 基础服务接口 T规定  入参
 */
public interface IBaseRepository<T> {

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

    /**
     * 根据id查询
     * @param id  object：id可能为str或Integer或...
     * @return
     */
    T selectById(Object id);

    /**
     * 根据ids查询
     * @param ids
     * @return
     */
    List<T> selectByIds(List ids);

    /**
     * 只查询一条
     * @param con
     * @return
     */
    T selectOne(Object con);

    /**
     * 万能eq查询 根据con查询 所有条件均是eq
     * @param con
     * @return
     */
    List<T> selectByCon(Object con);
}
