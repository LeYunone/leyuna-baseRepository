package com.leyuna.base.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyuna.base.iservice.IBaseRepository;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class BaseRepository2<M extends BaseMapper<DO>, DO, T> extends ServiceImpl<M, DO> implements IBaseRepository<DO> {

    private Class<?> do_Class;

    private Class<?> t_Class;

    /**
     * 转换器
     */
    private Map<Class<?>, Method> covers;

    /**
     * 集合转换器 list
     * 规则：List > Object ，非唯一场景中，当存在List转换模式时，优先考虑list，
     */
    private Map<Class<?>, Method> collectionCovers;

    private Object mapperCover;

    public BaseRepository2() {
        Class<?> clazzz = getClass();
        Type generic = clazzz.getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) generic).getActualTypeArguments();
            //DO
            this.do_Class = (Class<?>) actualTypeArguments[1];
            //虚泛
            this.t_Class = (Class<?>) actualTypeArguments[2];
        }
        //如果虚泛是一个接口，默认当做Convert
        if (t_Class.isInterface()) {
            //转换器
            this.mapperCover = Mappers.getMapper(t_Class);
            this.covers = new HashMap<>();
            this.collectionCovers = new HashMap<>();
            Method[] methods = mapperCover.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals("equals")) continue;
                Class<?> returnType = method.getReturnType();
                //入参
                Class<?>[] parameterTypes = method.getParameterTypes();
                //拉取DO -> T 的转换模式
                if (1 == parameterTypes.length && parameterTypes[0].isAssignableFrom(do_Class)) {
                    covers.put(returnType, method);
                } else {
                    //集合分支
                    if (0 != parameterTypes.length && Collection.class.isAssignableFrom(parameterTypes[0])) {
                        Type[] genericParameterTypes = method.getGenericParameterTypes();

                        //出参泛型
                        if (Collection.class.isAssignableFrom(returnType)) {
                            Type genericSuperclass = method.getGenericReturnType();
                            if (genericSuperclass instanceof ParameterizedType) {
                                Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                                if (actualTypeArguments.length > 1) continue;
                                returnType = (Class<?>) actualTypeArguments[0];
                            }
                        }

                        //集合泛型
                        if (null != genericParameterTypes && 1 == genericParameterTypes.length && genericParameterTypes[0] instanceof ParameterizedType) {
                            //泛型存在
                            ParameterizedType pt = (ParameterizedType) genericParameterTypes[0];
                            if (pt.getActualTypeArguments().length > 1) continue;
                            Class<?> tempC = (Class<?>) pt.getActualTypeArguments()[0];
                            if (pt.getActualTypeArguments().length == 1 && tempC.isAssignableFrom(do_Class)) {
                                collectionCovers.put(returnType, method);
                            }
                        }
                    }
                }
            }
            //锁住转化器
            covers = Collections.unmodifiableMap(covers);
            collectionCovers = Collections.unmodifiableMap(collectionCovers);
        }
    }

    /**
     * 根据实体类更新
     *
     * @param o
     * @return
     */
    @Override
    public boolean updateById(Object o) {
        DO d = o.getClass().isAssignableFrom(do_Class) ? (DO) o : castToDO(o);
        return super.updateById(d);
    }

    /**
     * 创建实体
     *
     * @param entity
     * @return
     */
    @Override
    public boolean insertOrUpdate(Object entity) {
        DO aDo = castToDO(entity);
        return this.saveOrUpdate(aDo);
    }

    /**
     * id删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * id 逻辑删除 TODO
     *
     * @param id
     * @return
     */
    @Override
    public <R> boolean deleteLogicById(Serializable id, SFunction<DO, R> tableId,SFunction<DO,R> deleted) {
        UpdateWrapper<DO> updateWrapper = new UpdateWrapper<DO>();
        LambdaUpdateWrapper<DO> lambda = updateWrapper.lambda();
        lambda.eq(tableId, id);
        //逻辑为1时删除
        lambda.set(deleted,1);
        return super.update(lambda);
    }

    @Override
    public DO selectOne(Object o) {
        return (DO) this.selectOne(o, do_Class);
    }

    /**
     * 根据条件只查询一条
     *
     * @param o
     * @param clazz
     * @return
     */
    @Override
    public <R> R selectOne(Object o, Class<R> clazz) {
        List<R> rs = this.selectByCon(o, clazz);
        if (CollectionUtils.isNotEmpty(rs)) {
            return rs.get(0);
        } else {
            return null;
        }
    }

    @Override
    public DO selectById(Serializable id) {
        return (DO) this.selectById(id, do_Class);
    }

    /**
     * 主键id查询
     *
     * @param id
     * @param clazz
     * @param <R>
     * @return
     */
    @Override
    public <R> R selectById(Serializable id, Class<R> clazz) {
        if (!covers.containsKey(clazz)) {
            return (R) this.baseMapper.selectById(id);
        }
        DO aDo = this.baseMapper.selectById(id);
        Object o = this.castCover(this.covers, clazz, aDo);
        return (R) o;
    }

    /**
     * 默认出来DO
     *
     * @param o
     * @return
     */
    @Override
    public List<DO> selectByCon(Object o) {
        return (List<DO>) this.selectByCon(o, do_Class);
    }

    @Override
    public List<DO> selectByCon(LambdaQueryWrapper<DO> queryWrapper) {
        DO aDo = castToDO(null);
        return (List<DO>) this.selectByCon(aDo, do_Class, queryWrapper);
    }

    @Override
    public List<DO> selectByCon(Object o, LambdaQueryWrapper<DO> queryWrapper) {
        return (List<DO>) this.selectByCon(o, do_Class, queryWrapper);
    }

    /**
     * 自定义出来clazz对象
     *
     * @param o
     * @param clazz
     * @return
     */
    @Override
    public <R> List<R> selectByCon(Object o, Class<R> clazz) {
        return this.selectByCon(o, clazz, null);
    }

    @Override
    public <R> List<R> selectByCon(Object o, Class<R> clazz, LambdaQueryWrapper<DO> queryWrapper) {
        deletedToFalse(o);
        DO d = castToDO(o);
        List<DO> dos = this.getConQueryResult(d, queryWrapper);
        if (!covers.containsKey(clazz)) {
            //出DO
            return (List<R>) dos;
        }
        //target class
        return (List<R>) this.castCover(this.collectionCovers, clazz, dos);
    }

    @Override
    public Page<DO> selectByConPage(Object o, Page page) {
        LambdaQueryWrapper<DO> lambdaQueryWrapper = new LambdaQueryWrapper(o);
        return (Page<DO>) this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

    public <R> Page<R> selectByConPage(Object o, Class<R> clazz, Page page) {
        Page<DO> doPage = this.selectByConPage(o, page);
        List<R> cover = (List<R>) castCover(this.collectionCovers, clazz, doPage.getRecords());
        Page<R> rPage = new Page<>(doPage.getCurrent(),doPage.getSize(),doPage.getTotal());
        rPage.setRecords(cover);
        rPage.setOrders(doPage.getOrders());
        rPage.setPages(doPage.getPages());
        return rPage;
    }

    /**
     * 查询规则：条件对象 与 自定义Lambda条件 AND 拼接
     *
     * @param o
     * @param queryWrapper
     * @return
     */
    private List<DO> getConQueryResult(Object o, LambdaQueryWrapper<DO> queryWrapper) {
        if (null == queryWrapper) {
            queryWrapper = new LambdaQueryWrapper<>();
        }
        DO aDo = castToDO(o);
        queryWrapper.setEntity(aDo);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * 执行转换
     *
     * @param clazz
     * @param o
     * @return
     */
    private Object castCover(Map<Class<?>, Method> cover, Class<?> clazz, Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        //选取分支
        boolean isList = cover == this.collectionCovers ? true : false;
        Method method = cover.get(clazz);
        //复数循环
        for (int i = 0; method == null && i <= 1; i++) {
            cover = isList ? this.covers : null;
            method = cover.get(clazz);
        }
        if (method == null) {
            return null;
        }
        try {
            if (Collection.class.isAssignableFrom(method.getParameterTypes()[0]) && !Collection.class.isAssignableFrom(o.getClass())) {
                //强行同步类型
                List arr = new ArrayList();
                arr.add(o);
                o = arr;
            }
            Object invoke = method.invoke(this.mapperCover, o);
            if (!isList && Collection.class.isAssignableFrom(invoke.getClass())) {
                //如果不是集合 但是走了集合分支
                return  ((Collection) invoke).iterator().next();
            }
            if(isList && !Collection.class.isAssignableFrom(invoke.getClass())){
                //如果是集合 但是走了单数分支
                List<Object> list = new ArrayList<>();
                list.add(invoke);
                return list;
            }
            return invoke;
        } catch (Exception e) {
        }
        return null;
    }

    private DO castToDO(Object o) {
        if (null != o && o.getClass().isAssignableFrom(do_Class)) {
            return (DO) o;
        }
        DO d = null;
        try {
            d = (DO) do_Class.newInstance();
        } catch (Exception e) {
        }
        if (null != o) {
            BeanUtil.copyProperties(o,d);
        }
        return d;
    }

    /**
     * 如果包含字段deleted，且值为空，那么给上默认值0
     *
     * @param con
     */
    private void deletedToFalse(Object con) {
        Class<?> aClass = con.getClass();
        try {
            Field deletedField = aClass.getDeclaredField("isDeleted");
            boolean accessible = deletedField.isAccessible();
            try {
                if (!accessible) {
                    deletedField.setAccessible(true);
                }
                Object value = deletedField.get(con);
                if (value == null) {
                    deletedField.set(con, 0);
                }
            } finally {
                if (!accessible) {
                    deletedField.setAccessible(accessible);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
    }
}
