<div style="text-align:center;">
<h2>配置说明</h2>
</div>

#### 全局配置

    {
        "database"          : 'oracle',                   # 数据库类型,缺省为oracle
        "database_ip"       : "10.25.76.190",             # 数据库地址
        "database_port"     : 1521,                       # 数据库端口
        "database_service"  : "erpdvlp",                  # 数据库服务名
        "database_username" : "simdvlp",                  # 数据库用户名
        "database_password" : "simdvlp",                  # 数据库用户密码

        "svn_repo_path"     : "/home/ns/dev/bssim",       # svn版本库的本地路径
        "svn_debug"         : false,                      # 是否打开svn的调试信息

        # 在此注册数据表，按schema分组
        "user_tables"       : [{
           "user"          : "XSSO",                      # schema名称
           "tables"        : ["TSOSOA1", "TSOSOA4"]       # 数据表
        },{
            "user"          : "XSSD",
            "tables"        : ["TSDSCC1","TSDSCC2","TSDWF01","TSDSD01","TSDSD02"]
        },{
            "user"          : "XSSM",
            "tables"        : ["TSMSR01", "TSMSR02"]
        },{
            "user"          : "XSTB",
            "tables"        : ["TTBWF01"]
        },{
            "user"          : "XSSA",
            "tables"        : ["TSA01","TSASA01", "TSASA02", "TSASA03", "TSASS01", "TSASS02", "TSASS03"]
        }]
    }

_注意：在使用以上内容作为配置模板时，请去除 `# 说明文字` 。_

<br />

#### 数据表配置

1. **belongs_to**配置

    假设有2张数据表，分别为A表和B表。A表和B表是一对多关系。按照规范，B表中必有一个名为 `A_ID` 的字段，用于B表的记录关联A表的记录。对于B表来说，这种关联情形称作 **B属于A** 。

        # 在B表中的配置
        {
            "belongs_to": [ "A" ]
        }

2. **has_many**配置

    仍然是上面的这个假设。对于A表来说，这种关联情形称作 **A有多个B** 。

        # 在A表中的配置
        {
            "has_many": [ "B" ]
        }

3. **has_one**配置

    还是假设有2张数据表，A和B。A表和B表是一对一关系。按照规范，会有2种可能的情况：

    + 在A表中有一个字段 `B_ID`，记录其对应的一条B表的记录。对于B表来说，这种关联情形称作 **B有一个A** 。

         # 在B表中的配置
         {
             "has_one": [ "A" ]
         }

    + 在B表中有一个字段 `A_ID`，记录其对应的一条A表的记录。对于A表来说，这种关联情形称作 **A有一个B** 。

         # 在A表中的配置
         {
             "has_one": [ "B" ]
         }

4. **多对多关联**配置

    假设有2张数据表，分别是A和B。A表多对多B表。为了能表达这种关系，需要引入第三张表 -- C表 。在C表上，设置2个关联字段，分别是 `A_ID` 和 `B_ID` 。通过C表，将多对多的关系转换为2个一对多的关系，描述如下：

    + A 有多个 C
    + C 属于 A
    + B 有多个 C
    + C 属于 B

        # 在A表中的配置
        {
            "has_many" : [ "C" ]
        }

        # 在B表中的配置
        {
            "has_many" : [ "C" ]
        }

        # 在C表中的配置
        {
            "belongs_to" : [ "A", "B" ]
        }

5. **package**配置

    _在下一个版本中，可以通过配置，指定生成代码所属的包名前缀。_

        # 将A表放置于 com.baosight.slms 为前缀的包名下，而不是默认的 com.baosight.bssim
        {
            "package": "com.baosight.slms"
        }

<br />

[帮助文档首页](/doc/main)