var bssim = angular.module('Bssim', ['ngRoute', 'ngSanitize']);

bssim.config(function($routeProvider){
    $routeProvider.when("/", {
        templateUrl: "views/tables.html",
        controller: "TablesController"
    }).when("/config", {
        templateUrl: "views/config.html",
        controller: "ConfigController"
    }).when("/tables/:fullTable", {
        templateUrl: "views/table.html",
        controller: "TableController"
    }).when("/docs/:docName", {
        templateUrl: "views/doc.html",
        controller: "DocController"
    });
});

bssim.factory("Tables", function($http){
    return {
        "all" : function(){
            return $http({"method":"GET", url:"tables"});
        }
    }
});
bssim.factory("Config", function($http){
    return {
        "get" : function(){
            return $http({"method":"GET", url:"config"});
        },
        "save" : function(data) {
            console.log(data);
            return $http({"method":"POST", url:'config', "data":data});
        }
    }
});

bssim.factory("Noty", function($rootScope, $location){
    $rootScope.$on('$routeChangeStart', function(){
        $.noty.closeAll();
    });

    return {
        "info": function(message, path){
            path = path || '/';
            if(path == $location.path()) {
                noty({"text":message, "timeout":1500});
            }
        },
        "success": function(message, path){
            path = path || '/';
            if(path == $location.path()) {
                noty({"text":message, "type":"success", "timeout":1500});
            }
        },
        "error": function(message, path){
            path = path || '/';
            if(path == $location.path()) {
                noty({"text":message, "type":"error", "timeout":6000});
            }
        },
        "loading": function(message, path){
            console.log(path);
            path = path || '/';
            if(path == $location.path()) {
                noty({
                    "text":message,
                    "template":'<div class="noty_message"><img src="assets/img/loading.gif" width="14" height="14" class="pull-left"/><span class="noty_text"></span><div class="noty_close"></div></div>'
                });
            }
        },
        "closeAll": function() {
            $.noty.closeAll();
        }
    }
});