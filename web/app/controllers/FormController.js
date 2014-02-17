bssim.controller("FormController", function($scope, $routeParams, Noty, $http, $location){
    var schemaTable = $routeParams["schemaTable"];
    var config = angular.fromJson($routeParams["config"]);
    var path = $location.path();

    $scope.formName = config.formName;

    var jspCm = CodeMirror(document.getElementById("jsp_div"), {
        mode : 'application/x-jsp',
        theme : 'eclipse'
    });
    jspCm.setSize('100%', 'auto');

    var jsCm = CodeMirror(document.getElementById("js_div"), {
        mode : 'javascript',
        theme : 'eclipse'
    });
    jsCm.setSize('100%', 'auto');

    var serviceCm = CodeMirror(document.getElementById("service_div"), {
        mode : 'text/x-java',
        theme : 'eclipse'
    });
    serviceCm.setSize('100%', 'auto');

    $scope.showJs = function(){
        $('#code_div > div').hide();
        $('#js_div').show();
        jsCm.refresh();
    };

    $scope.showService = function(){
        $('#code_div > div').hide();
        $('#service_div').show();
        serviceCm.refresh();
    };

    $scope.showJsp = function(){
        $('#code_div > div').hide();
        $('#jsp_div').show();
        jspCm.refresh();
    };

    Noty.loading('Loading...', path);
    $http.get("/form/"+schemaTable+"/"+angular.toJson(config)).success(function(result){
        if(!result.status){
            Noty.error(result.message, path);
        } else{
            Noty.closeAll();
            jspCm.setValue(result['data']['jspCode']);
            jsCm.setValue(result['data']['jsCode']);
            serviceCm.setValue(result['data']['serviceCode']);
        }
    }).error(function(){
        Noty.error("网络异常...", path);
    });

    $scope.commit = function(){
        Noty.loading('正在提交...', path);

        $http.post('form/'+schemaTable+'/commit/'+angular.toJson(config)).success(function(result){
            if(result.status){
                Noty.success(result.message, path, 4000);
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(){
            Noty.error('网络异常...', path);
        });
    };

    $scope.navToFormName = function(){
        $location.path("/form/name/"+schemaTable+"/{}");
    };
});