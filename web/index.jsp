<%@page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html ng-app="Bssim">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="app/bower_components/bootstrap/dist/css/bootstrap.min.css">
    <script src="app/bower_components/angular/angular.js"></script>
    <script src="app/bower_components/angular-route/angular-route.min.js"></script>
    <style>
        .hover-blue-border:hover {
            border-color: #428bca;
        }
        .active-blue-background:active {
            background: linear-gradient(to bottom,#428bca 0,#357ebd 100%) repeat-x;
            color: #ffffff;
        }
    </style>
    <script>
        var bssim = angular.module('Bssim', ['ngRoute']);

        bssim.config(function($routeProvider){
            $routeProvider.when("/", {
                templateUrl: "views/tables.html",
                controller: "TablesController",
                resolve: {

                }
            }).when("/config", {
                templateUrl: "views/config.html"
            });
        });

        bssim.factory("Tables", function($http){
            return {
                "all" : function(){
                    return $http({"method":"GET", url:"tables"});
                }
            }
        });

        bssim.controller("TablesController", function($scope, Tables){
            $scope.searchWord = "";

//            Tables.all().success(function(result){
//                $scope.groups = $scope.originGroups = result.data;
//            });
            $scope.groups = $scope.originGroups = {"XSSO":[{"TABLE_NAME":"TSOSOA1","COLUMN_COUNT":69,"INDEX_COUNT":1,"COMMENTS":"销售订单表","OWNER":"XSSO"},{"TABLE_NAME":"TSOSOA4","COLUMN_COUNT":143,"INDEX_COUNT":1,"COMMENTS":"销售订单子项表","OWNER":"XSSO"}],"XSSM":[{"TABLE_NAME":"TSMSR01","COLUMN_COUNT":24,"INDEX_COUNT":2,"COMMENTS":"产品需求表","OWNER":"XSSM"},{"TABLE_NAME":"TSMSR02","COLUMN_COUNT":9,"INDEX_COUNT":1,"COMMENTS":"产品需求附加属性表","OWNER":"XSSM"}],"XSSD":[{"TABLE_NAME":"TSDSCC1","COLUMN_COUNT":10,"INDEX_COUNT":2,"COMMENTS":"通用配置信息路径表","OWNER":"XSSD"},{"TABLE_NAME":"TSDSCC2","COLUMN_COUNT":10,"INDEX_COUNT":2,"COMMENTS":"通用配置信息键值表","OWNER":"XSSD"},{"TABLE_NAME":"TSDSD01","COLUMN_COUNT":20,"INDEX_COUNT":2,"COMMENTS":"组织机构信息表","OWNER":"XSSD"},{"TABLE_NAME":"TSDSD02","COLUMN_COUNT":11,"INDEX_COUNT":2,"COMMENTS":"组织隶属关系表","OWNER":"XSSD"},{"TABLE_NAME":"TSDWF01","COLUMN_COUNT":16,"INDEX_COUNT":3,"COMMENTS":"流程实例记录","OWNER":"XSSD"}],"XSTB":[{"TABLE_NAME":"TTBWF01","COLUMN_COUNT":10,"INDEX_COUNT":1,"COMMENTS":"测试工作流表01","OWNER":"XSTB"}],"XSSA":[{"TABLE_NAME":"TSA01","COLUMN_COUNT":49,"INDEX_COUNT":1,"COMMENTS":"核销明细账表","OWNER":"XSSA"},{"TABLE_NAME":"TSASA01","COLUMN_COUNT":26,"INDEX_COUNT":1,"COMMENTS":"货款信息表","OWNER":"XSSA"},{"TABLE_NAME":"TSASA02","COLUMN_COUNT":9,"INDEX_COUNT":1,"COMMENTS":"货款信息附加属性表","OWNER":"XSSA"},{"TABLE_NAME":"TSASA03","COLUMN_COUNT":23,"INDEX_COUNT":1,"COMMENTS":"折扣信息表","OWNER":"XSSA"},{"TABLE_NAME":"TSASS01","COLUMN_COUNT":29,"INDEX_COUNT":1,"COMMENTS":"内销发票表","OWNER":"XSSA"},{"TABLE_NAME":"TSASS02","COLUMN_COUNT":17,"INDEX_COUNT":1,"COMMENTS":"内销发票明细表","OWNER":"XSSA"},{"TABLE_NAME":"TSASS03","COLUMN_COUNT":20,"INDEX_COUNT":1,"COMMENTS":"销货清单表","OWNER":"XSSA"}]};

            $scope.$watch("searchWord", function(newValue){
                console.log(newValue);
                if(newValue == "") {
                    $scope.groups = $scope.originGroups;
                } else {
                    var result = {};
                    $.each($scope.originGroups, function(schema, tables){
                        $.each(tables, function(index, table){
                            if(table.TABLE_NAME.indexOf(newValue.toUpperCase()) > -1 || table.COMMENTS.indexOf(newValue) > -1) {
                                if(!result[schema]){
                                    result[schema] = [];
                                }
                                result[schema].push(table);
                            }
                        });
                    });
                    $scope.groups = result;
                }
            });

            $scope.tableClicked = function(){
                document.location.href = "config"
            };
        });
    </script>
</head>
<body>
<!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Bssim辅助开发工具</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#/"><i class="glyphicon glyphicon-home"></i> 首页</a></li>
                    <li><a href="#/config"><i class="glyphicon glyphicon-cog"></i> 设置</a></li>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="row" style="margin-top: 60px;">
        <div ng-view class="col-xs-10 col-xs-offset-1"></div>
    </div>

    <script src="app/bower_components/jquery/jquery.min.js"></script>
    <script src="app/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>