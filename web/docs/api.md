<style>
    a[name] { padding-top: 40px; margin-top: -40px; }
</style>

<div style="text-align:center;">
<h2>API说明</h2>
</div>

#### 创建

- [+ ModelClass newInstance()](#newInstance)
- [+ ModelClass newInstance(Map attr)](#newInstance_attr)

#### 从数据库读取

- [+ ModelClass find(Long id)](#find_long_id)
- [+ ModelClass findWithLock(Long id)](#findWithLock_long_id)
- [+ ModelClass find(String id)](#find_string_id)
- [+ ModelClass findWithLock(String id)](#findWithLock_string_id)
- <a href="#find_long_ids">+ ModelClass[] find(Long[] ids)</a>
- <a href="#findWithLock_long_ids">+ ModelClass[] findWithLock(Long[] ids)</a>
- [+ CustomQuerier q(String sqlmap, Map arg)](#q_sqlmap_arg)
- [+ ModelQuerier where(Map arg)](where_arg)

#### 其他

- [- int getVersion()](#getVersion)

-------------------------------------------------------------------------------------------------

#### <a name="newInstance">+ ModelClass newInstance()</a>

创建并返回类型是 `ModelClass` 的空的Model对象(无ID)。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    aModel.setRemark("这是一个样例");

#### <a name="newInstance_attr">+ ModelClass newInstance(Map attr)</a>

创建并返回类型是 `ModelClass` 的Model对象(无ID)。在返回之前，使用参数对这个Model对象做初始化。

    Map attr = new HashMap();
    attr.put("remark", "这是一个样例");
    Tsmsr01 aModel = Tsmsr01.newInstance(attr);

#### <a name="find_long_id">+ ModelClass find(Long id)</a>

从数据库的对应表中读取ID与传入参数相同的一条记录，用其创建并返回类型是 `ModelClass` 的Model对象。如果数据库中不存在ID相同的记录，那么返回null。

    Long id = 1L;
    Tsmsr01 aModel = Tsmsr01.find(id);
    if (aModel != null) {
        // ...
    }

#### <a name="findWithLock_long_id">+ ModelClass findWithLock(Long id)</a>

功能同 [+ ModelClass find(Long id)](#find_long_id) ，但在返回后会把对应的记录锁住，防止被其他程序修改，直至事务提交或回滚。 _请谨慎使用，避免发生数据表死锁。_

    Long id = 1L;
    Tsmsr01 aModel = Tsmsr01.findWithLock(id);
    if (aModel != null) {
        // ...
    }

#### <a name="find_string_id">+ ModelClass find(String id)</a>

功能同 [+ ModelClass find(Long id)](#find_long_id)，但传入参数使用了String类型。

    String id = inInfo.get("id")+"";
    Tsmsr01 aModel = Tsmsr01.find(id);
    if (aModel != null) {
        // ...
    }

#### <a name="findWithLock_string_id">+ ModelClass findWithLock(String id)</a>

功能同 [+ ModelClass findWithLock(Long id)](#findWithLock_long_id)，但传入参数使用了String类型。

    String id = inInfo.get("id")+"";
    Tsmsr01 aModel = Tsmsr01.findWithLock(id);
    if (aModel != null) {
        // ...
    }

#### <a name="find_long_ids">+ ModelClass[] find(Long[] ids)</a>

功能类似 [+ ModelClass find(Long id)](#find_long_id)，但参数是ID数组，可以一次从数据库中读取多笔记录，返回类型是 `ModelClass` 的数组。如果，结果集的记录数和参数数组的元素数不一致，则抛出ModelException异常。

    Long[] ids = new Long[] { 1, 2, 3 };
    Tsmsr01[] models = Tsmsr01.find(ids);
    for (int i=0; i<models.length; i++) {
        // ...
    }

#### <a name="findWithLock_long_ids">+ ModelClass[] findWithLock(Long[] ids)</a>

功能同 <a href="#find_long_ids">+ ModelClass[] find(Long[] ids)</a>，但在返回后会把对应的记录锁住，防止被其他程序修改，直至事务提交或回滚。 _请谨慎使用，避免发生数据表死锁。_

    Long[] ids = new Long[] { 1, 2, 3 };
    Tsmsr01[] models = Tsmsr01.findWithLock(ids);
    for (int i=0; i<models.length; i++) {
        // ...
    }

#### <a name="q_sqlmap_arg">+ CustomQuerier q(String sqlmap, Map arg)</a>

返回一个自定义查询器。

    List result = Tsmsr01.q(sqlmap, arg).all();
    // 等价于
    List result = dao.query(sqlmap, arg);

#### <a name="where_arg">+ ModelQuerier where(Map arg)</a>

返回一个Model查询器。参数中的键值对会转换为 `key1=value1 AND key2=value2 AND ... AND keyn=valuen` 这样的SQL片段，作为where子句的一部分。

    Map arg = new HashMap();
    arg.put("companyCode", "DS");
    arg.put("periodNum", "201401");
    /*
        转换后的SQL：

        SELECT ...
        FROM   TSMSR01
        WHERE  COMPANY_CODE = 'DS'
          AND  PEROIDNUM    = '201401'
     */
    Tsmsr01 aModel = Tsmsr01.where(arg).first();   // 取符合条件的第一条记录

#### [- int getVersion()](#getVersion)

得到Model实例的版本号。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    System.out.println(aModel.getVersion());

<br />

**(未完)**

<br />

[帮助文档首页](/doc/main)