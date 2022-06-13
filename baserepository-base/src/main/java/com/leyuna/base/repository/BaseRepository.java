package com.leyuna.base.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyuna.base.iservice.IBaseRepository;
import com.leyuna.common.util.TransformationUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象Repository服务类
 *
 * @author pengli
 * @since 2022-03-28
 * 基础服务类1  需要调节 - DO[实体]  CO[出参]  M[mapper]
 */
public abstract class BaseRepository<M extends BaseMapper<DO>, DO> extends ServiceImpl<M, DO> implements IBaseRepository<DO> {
    private Class COclass;
    private Class DOclass;

    public BaseRepository () {
        Class<?> c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) t).getActualTypeArguments();
            DOclass = (Class<?>) params[1];
            COclass = (Class<?>) params[2];
        }
    }

}
