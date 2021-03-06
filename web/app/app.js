var bssim = angular.module('Bssim', ['ngRoute', 'ngSanitize']);

bssim.config(function($routeProvider){
    $routeProvider.when("/", {
        templateUrl: "app/views/tables.html",
        controller: "TablesController"
    }).when("/config", {
        templateUrl: "app/views/config.html",
        controller: "ConfigController"
    }).when("/tables/:fullTable", {
        templateUrl: "app/views/table.html",
        controller: "TableController"
    }).when("/docs/:docName", {
        templateUrl: "app/views/doc.html",
        controller: "DocController"
    }).when("/form/name/:schemaTable/:config", {
        templateUrl: "app/views/form_name.html",
        controller: "FormNameController"
    }).when("/form/query/:schemaTable/:config", {
        templateUrl: "app/views/form_query.html",
        controller: "FormQueryController"
    }).when("/form/:schemaTable/:config", {
        templateUrl: "app/views/form.html",
        controller: "FormController"
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
            return $http({"method":"POST", url:'config', "data":data});
        }
    }
});

bssim.factory("Noty", function($rootScope, $location){
    $rootScope.$on('$routeChangeStart', function(){
        $.noty.closeAll();
    });

    return {
        "info": function(message, path, timeout){
            path = path || '/';
            timeout = timeout || 2000;
            if(path == $location.path()) {
                noty({"text":message, "timeout":timeout});
            }
        },
        "success": function(message, path, timeout, option){
            path = path || '/';
            timeout = timeout || 2000;
            if(path == $location.path()) {
                noty($.extend({"text":message, "type":"success", "timeout":timeout}, option));
            }
        },
        "error": function(message, path){
            path = path || '/';
            var timeout =  6000;
            if(path == $location.path()) {
                noty({"text":message, "type":"error", "timeout":timeout});
            }
        },
        "loading": function(message, path, option){
            path = path || '/';
            if(path == $location.path()) {
                noty($.extend({
                    "text":message,
                    "template":'<div class="noty_message"><img src="assets/img/loading.gif" width="14" height="14" class="pull-left"/><span class="noty_text"></span><div class="noty_close"></div></div>'
                }, option));
            }
        },
        "closeAll": function() {
            $.noty.closeAll();
        }
    }
});