# BaseRepository

基于mybatisplus的持久层基类，更大限度的降低简单CRUD代码量。

## 目前的功能

BaseRepository

```JAVA
public abstract class BaseRepository<M extends BaseMapper<DO>, DO, CO>
       extends ServiceImpl<M, DO> implements IBaseRepository<CO>{}
```



**1、insertOrUpdate (Object entity)**

插入以及更新，原Mybatis功能

**2、batchCreate（List list)**

批量插入，原Mybatis功能

**3、delete（Object id）**

id删除，原Mybatis功能

**4、batchDelete（List ids）**

ids批量删除，原Mybatis功能

**5、selectById（Object id）**

id查询，原Mybatis功能

**6、selectByIds（List ids）**

ids查询，原Mybatis功能

**7、selectOne（Object con）**

根据con为eq条件 ，属性均Eq字段值，null则略过，查询出一条

**8、selectByCon (Object con)**

根据con为eq条件 ，属性均Eq字段值，null则略过，全量查询

**9、selectByConOrder (String condition, boolean isDesc, Object con)**

根据con为eq条件，condition和isDesc为排序条件和是否Desc倒序 ，属性均Eq字段值，null则略过，全量查询

10、**selectByPage (Object con, Integer index, Integer size)**

根据con为eq条件 ，属性均Eq字段值，null则略过，分页查询

11、**selectByConOrderPage (Object con, Integer index, Integer size, String condition, boolean isDesc)**

根据con为eq条件，condition和isDesc为排序条件和是否Desc倒序 ，属性均Eq字段值，null则略过，分页查询

12、**selectLike (Object con, String field,String likeName)**
根据con为eq条件，模糊查询field字段，likeName为模糊值，全量查询
重载(Object con, boolean isDesc,String field,String likeName)
排序，isDesc判断是否是desc排，默认asc

13、**selectLikeByPage (Object con, Integer index, Integer size,String field,String likeName);**
根据con为eq条件，模糊查询field字段，llikeName为模糊值，分页查询