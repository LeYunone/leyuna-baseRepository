package com.leyuna.base.iservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author pengli
 * @date 2022-04-05
 * 封装查询接口
 */
public interface IQueryService<T> extends IQueryPageService{

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

    /**
     * 万能eq排序查询 根据condition和isDesc指定排序条件
     * @param condition
     * @param con
     * @return
     */
    List<T> selectByConOrder(String condition,boolean isDesc,Object con);
    
}
