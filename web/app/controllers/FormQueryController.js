bssim.controller("FormQueryController", function($scope, $routeParams, Noty, $http, $location){
    var schemaTable = $routeParams["schemaTable"];
    var config = angular.fromJson($routeParams["config"]);
    var path = $location.path();

    Noty.loading('Loading...', path);

    $http.get("/tables/"+schemaTable+"/columns").success(function(result){
        if(!result.status){
            Noty.error(result.message, path);
        } else{
            Noty.closeAll();
            $scope.columns = result.data.columns;
        }
    }).error(function(){
        Noty.error("网络异常...", path);
    });

    $scope.navToFormName = function(){
        $location.path("/form/name/"+schemaTable+"/"+angular.toJson(config));
    };

    $scope.navToFormCode = function(){
        var queryList = [];
        for(var i=0; i<$scope.columns.length; i++){
            if($scope.columns[i].checked){
                queryList.push($scope.columns[i].meta.name);
            }
        }
        config.queryList = queryList;

        config.formName = (config.firstModule+config.secondModule+config.no+config.no_extension).toUpperCase();

        $location.path("/form/" + schemaTable + "/" + angular.toJson(config));
    };

});