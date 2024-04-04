package com.leyuna.base.iservice;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengli
 * @create 2022-03-28 16:54
 * 基础服务接口 T规定  入参
 */
public interface IBaseRepository<DO> extends IService<DO>,IOperationService<DO>, IQueryService<DO> {

}
