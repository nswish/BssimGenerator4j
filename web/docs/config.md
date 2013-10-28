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

(未完)

<br />

[帮助文档首页](/doc/main)