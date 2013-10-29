<div style="text-align:center;">
<h2>API说明</h2>
</div>

#### 创建Model

- [+ &lt;T&gt; newInstance()](#newInstance)
- [+ &lt;T&gt; newInstance(Map attr)](#newInstance_attr)

#### 载入Model

- [+ &lt;T&gt; find(Long id)](#find_long_id)
- [+ &lt;T&gt; findWithLock(Long id)](#findWithLock_long_id)

#### 其他功能

- [- int getVersion()](#getVersion)

-------------------------------------------------------------------------------------------------

#### <a name="newInstance">+ &lt;T&gt; newInstance()</a>

创建并返回类型是 ``<T>`` 的空的Model对象(无ID)。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    aModel.setRemark("这是一个样例");

#### <a name="newInstance_attr">+ &lt;T&gt; newInstance(Map attr)</a>

创建并返回类型是 ``<T>`` 的Model对象(无ID)。在返回之前，使用参数对这个Model对象做初始化。

    Map attr = new HashMap();
    attr.put("remark", "这是一个样例");
    Tsmsr01 aModel = Tsmsr01.newInstance(attr);

#### <a name="find_long_id">+ &lt;T&gt; find(Long id)</a>

从数据库的对应表中读取ID与传入参数相同的一条记录，用其创建并返回类型是 ``<T>`` 的Model对象。如果数据库中不存在ID相同的记录，那么返回null。

    Long id = 1L;
    Tsmsr01 aModel = Tsmsr01.find(id);
    if (aModel != null) {
        // ...
    }

#### <a name="findWithLock_long_id">+ &lt;T&gt; findWithLock(Long id)</a>

功能同 [+ &lt;T&gt; find(Long id)](#find_long_id) ，但在返回后会把对应的记录锁住，防止被其他程序修改，直至commit。 _请谨慎使用，避免发生数据表死锁。_

    Long id = 1L;
    Tsmsr01 aModel = Tsmsr01.findWithLock(id);
    if (aModel != null) {
        // ...
    }

#### [- int getVersion()](#getVersion)

得到Model实例的版本号。

    Tsmsr01 aModel = Tsmsr01.newInstance();
    System.out.println(aModel.getVersion());

<br />

**(未完)**

<br />

[帮助文档首页](/doc/main)