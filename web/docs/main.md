<div style="text-align:center;">
<h2>帮助文档</h2>
</div>

#### 简介
BSSIM辅助开发工具是为销售物流研发项目开发的一种代码生成工具。用以简化开发工作，加快开发进度，带来更多的开发乐趣。

<br />

#### 特点
1. 100%兼容iPlat4j代码生成器。
2. 灵感来自于ruby的[activerecord项目](http://rubygems.org/gems/activerecord)，生成的代码在使用上与其有诸多相似之处。

<br />

#### 规范
1. 数据表必须有 `ID` 字段作为主键(数字型，自增)。
2. 数据表必须有 `REC_CREATOR` `REC_CREATE_TIME` `REC_REVISOR` `REC_REVISE_TIME` 这4个字段(字符型)用以记录 (创建/修改)的(人/时间)。将来的新版本，此项要求将不再作为必须项。
3. 数据表之间的关联必须使用命名形式为 `表名_ID` 的字段。

<br />

#### 配置说明
(未完)

<br />

#### API文档
(未完)

<br />

#### 源代码
作为开源软件，托管于[Github](https://github.com/nswish/BssimGenerator4j)。

这个软件远未成熟，期待您的宝贵建议([发邮件给作者](mailto:gulei@baosight.com))。