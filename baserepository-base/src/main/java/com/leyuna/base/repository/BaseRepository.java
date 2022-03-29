package com.leyuna.base.repository;

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
public abstract class BaseRepository<M extends BaseMapper<DO>, DO, CO> extends ServiceImpl<M, DO> implements IBaseRepository<CO> {
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

    /**
     * 创建实体
     *
     * @param entity
     * @return
     */
    @Override
    public CO insertOrUpdate (Object entity) {
        DO copy = (DO) TransformationUtil.copyToDTO(entity, DOclass);
        //使用ServiceImpl中
        this.saveOrUpdate(copy);
        return (CO) TransformationUtil.copyToDTO(copy, COclass);
    }

    /**
     * 批量创建
     *
     * @param list
     * @return
     */
    @Override
    public boolean batchCreate (List list) {
        List<DO> copy = TransformationUtil.copyToLists(list, DOclass);
        //使用ServiceImpl中
        return this.saveBatch(copy);
    }

    /**
     * id 删除
     *
     * @param id
     * @return
     */
    @Override
    public int delete (Object id) {
        return baseMapper.deleteById((Serializable) id);
    }

    /**
     * ids批量删除
     *
     * @param ids
     * @return
     */
    @Override
    public int batchDelete (List ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 根据ID查询出一个对象
     *
     * @param id
     * @return
     */
    @Override
    public CO selectById (Object id) {
        DO do_ = baseMapper.selectById((Serializable) id);
        return (CO) TransformationUtil.copyToDTO(do_, COclass);
    }

    /**
     * 根据ID列表批量查询
     *
     * @param ids
     * @return
     */
    @Override
    public List<CO> selectByIds (List ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<DO> DOS = baseMapper.selectBatchIds(ids);
        return TransformationUtil.copyToLists(DOS, COclass);
    }


    /**
     * 根据领域对象的设定的值来查询
     *
     * @param con
     * @return
     */
    @Override
    public CO selectOne (Object con) {
        List<CO> list = this.selectByCon(con);
        //非空
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据领域对象的设定的值来查询
     *
     * @param con
     * @return
     */
    @Override
    public List<CO> selectByCon (Object con) {
        Object copy = TransformationUtil.copyToDTO(con, DOclass);
        QueryWrapper<DO> dQueryWrapper = new QueryWrapper<DO>().
                allEq(TransformationUtil.transDTOColumnMap(copy), false);
        List<DO> ds = this.baseMapper.selectList(dQueryWrapper);
        return TransformationUtil.copyToLists(ds, COclass);
    }

    @Override
    public List<CO> selectByConOrder (String condition, boolean isDesc, Object con) {
        QueryWrapper queryWrapper = orderSql(condition, isDesc, con);
        List<DO> ds = this.baseMapper.selectList(queryWrapper);
        return TransformationUtil.copyToLists(ds, COclass);
    }

    private QueryWrapper orderSql (String condition, boolean isDesc, Object con) {
        Object copy = TransformationUtil.copyToDTO(con, DOclass);
        QueryWrapper<DO> dQueryWrapper = null;
        if (isDesc) {
            dQueryWrapper = new QueryWrapper<DO>().allEq(TransformationUtil.transDTOColumnMap(copy), false).orderByDesc(condition);
        } else {
            dQueryWrapper = new QueryWrapper<DO>().allEq(TransformationUtil.transDTOColumnMap(copy), false).orderByAsc(condition);
        }
        return dQueryWrapper;
    }

    @Override
    public Page<CO> selectByPage (Object con, Integer index, Integer size) {
        Page<DO> page = new Page<>(index, size);
        Object copy = TransformationUtil.copyToDTO(con, DOclass);
        QueryWrapper<DO> dQueryWrapper = new QueryWrapper<DO>().allEq(TransformationUtil.transDTOColumnMap(copy), false);
        IPage<DO> doPage = this.baseMapper.selectPage(page, dQueryWrapper);

        return TransformationUtil.copyToPage(doPage, COclass);
    }

    @Override
    public Page<CO> selectByConOrderPage (Object con, Integer index, Integer size, String condition, boolean isDesc) {
        Map<String, Object> stringObjectMap = TransformationUtil.transDTOColumnMap(con);
        QueryWrapper queryWrapper = orderSql(condition, isDesc, con);
        Page page = new Page(index, size);
        IPage<DO> ipage = this.baseMapper.selectPage(page, queryWrapper);
        return TransformationUtil.copyToPage(ipage, COclass);
    }
}
