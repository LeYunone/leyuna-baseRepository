package com.leyuna.base.iservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author pengli
 * @date 2022-04-05
 */
public interface IQueryPageService<T>{

    /**
     * 万能eq分页查询
     * @param con
     * @param index
     * @param size
     * @return
     */
    Page<T> selectByPage (Object con, Integer index, Integer size);

    /**
     * 万能eq分页查询 根据condition和isDesc排序查询
     * @param con
     * @param index
     * @param size
     * @param condition
     * @param isDesc
     * @return
     */
    Page<T> selectByConOrderPage(Object con,Integer index,Integer size,String condition,boolean isDesc);
}
