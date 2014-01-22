## API说明
<br />

#### 创建

- [+ ModelClass newInstance()](#newInstance)
- [+ ModelClass newInstance(Map attr)](#newInstance_attr)

#### 读取

- [+ ModelClass find(Long id)](#find_long_id)
- [+ ModelClass findWithLock(Long id)](#findWithLock_long_id)
- [+ ModelClass find(String id)](#find_string_id)
- [+ ModelClass findWithLock(String id)](#findWithLock_string_id)
- <a href="#find_long_ids">+ ModelClass[] find(Long[] ids)</a>
- <a href="#findWithLock_long_ids">+ ModelClass[] findWithLock(Long[] ids)</a>
- [+ CustomQuerier q(String sqlmap, Map arg)](#q_sqlmap_arg)
- [+ ModelQuerier where(Map arg)](#where_arg)
- [+ ModelQuerier where(String where, Map arg)](#where_where_arg)

#### 增删修

- [- void delete()](#delete)

#### 其他

- [- int getVersion()](#getVersion)
- [- String getFullTableName()](#getFullTableName)

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
          AND  PEROID_NUM   = '201401'
     */
    Tsmsr01 aModel = Tsmsr01.where(arg).first();   // 取符合条件的第一条记录

#### <a name="where_where_arg">+ ModelQuerier where(String where, Map arg)</a>

比 [+ ModelQuerier where(Map arg)](where_arg) 更为灵活的一个方法。第一个参数是作为where字句的sql语句片段，第二个参数用以替换第一个参数中定义的变量。

    String where = "COMPANY_CODE = #companyCode# AND PEROID_NUM = #periodNum#";
    Map arg = new HashMap();
    arg.put("companyCode", "DS");
    arg.put("periodNum", "201401");
     /*
         转换后的SQL：

         SELECT ...
         FROM   TSMSR01
         WHERE  COMPANY_CODE = 'DS'
           AND  PEROID_NUM   = '201401'
      */
    Tsmsr01 aModel = Tsmsr01.where(where, arg).first();   // 取符合条件的第一条记录

第一个参数支持<isNotEmpty>标签。

    String where = "COMPANY_CODE = #companyCode#"
                 + "<isNotEmpty property="peroidNum"> AND PEROID_NUM = #periodNum#</isNotEmpty>";
    Map arg = new HashMap();
    arg.put("companyCode", "DS");
     /*
         转换后的SQL：

         SELECT ...
         FROM   TSMSR01
         WHERE  COMPANY_CODE = 'DS'
      */
    Tsmsr01 aModel = Tsmsr01.where(where, arg).first();   // 取符合条件的第一条记录

_&lt;isNotEmpty>标签目前仅支持一个property属性。将来的新版本，会支持更多ibatis的标签和属性_。

#### <a name="getFullTableName">- String getFullTableName()</a>

返回Model实例映射的数据表全名。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    System.out.println(aModel.getFullTableName());  // 输出 XSSM.TSMSR01

#### <a name="delete">- void delete()</a>

从数据库中删除对应的记录。

    Long id = 1L;
    Tsmsr01 aModel = Tsmsr01.find(id);
    if (aModel != null) {
        aModel.delete();
        aModel = null;   // 此语句是一种安全写法，可省略。
                         // 原因：虽然在数据库中，记录已经被删除。但是此时，aModel变量引用的对象未作为垃圾被回收。
                         //      为了防止aModel被误用，此处将其置为null。作为副作用，该对象因为失去了所有引用，也会成为垃圾将被回收。
    }

#### [- int getVersion()](#getVersion)

返回Model实例的版本号。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    System.out.println(aModel.getVersion());

<br />

**(未完)**

<br />

[帮助文档首页](#/docs/main)