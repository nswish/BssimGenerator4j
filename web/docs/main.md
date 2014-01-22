## 帮助文档

<br />

#### 简介
BSSIM辅助开发工具是为销售物流研发项目开发的一种代码生成工具。用以简化开发工作，减少重复编码，加快开发进度，带来更多的开发乐趣。

<br />

#### 特点
1. 100%兼容iPlat4j代码生成器。
2. 受ruby的[activerecord项目](http://rubygems.org/gems/activerecord)启发，在使用上与其有诸多相似之处。

<br />

#### 规范
1. 数据表必须有一个名为 `ID` 的字段作为主键(数字型)和一个对应的名为 `表名_SEQ` 的Sequence对象。
2. 数据表必须有名为 `REC_CREATOR` `REC_CREATE_TIME` `REC_REVISOR` `REC_REVISE_TIME` 这4个字段(字符型)用以记录 (创建/修改)的(人/时间)。 _将来的新版本，此项要求将不再作为必须项。_
3. 数据表之间的关联必须使用命名形式为 `表名_ID` 的字段。
4. 字段类型必须是 __数字型__ 或者 __字符型__。 _将来的新版本，会支持更多的字段类型。_

<br />

#### 使用范围
1. 销售物流产品化项目及其衍生项目。
2. 使用Oracle数据库的项目。 _将来的新版本，将支持DB2等数据库_

<br />

#### 配置说明
[参见相关文档](#/docs/config)

<br />

#### API说明
[参见相关文档](#/docs/api)

<br />

#### 源代码
作为开源软件，托管于[Github](https://github.com/nswish/BssimGenerator4j)。

这个软件远未成熟，期待您的宝贵建议([发邮件给作者](mailto:gulei@baosight.com))。