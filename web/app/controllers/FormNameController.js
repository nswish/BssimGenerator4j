bssim.controller("FormNameController", function($scope, $routeParams, $location){
    $scope.config = $.extend({}, angular.fromJson($routeParams['config']));
    var schemaTable = $routeParams['schemaTable'];
    var table = schemaTable.split(".")[1];

    if($.isEmptyObject($scope.config)){
        $scope.config.firstModule = table.slice(1,3);

        $scope.config.secondModule = table.slice(3, 5);
        if(!$scope.config.secondModule.match(/^[A-Z]/)){
            $scope.config.secondModule = "";
            $scope.config.no = table.slice(3);
        } else {
            $scope.config.no = table.slice(5);
        }

        $scope.config.no_extension = "";
        if($scope.config.no.length > 2) {
            $scope.config.no_extension = $scope.config.no.slice(2, 4);
            $scope.config.no = $scope.config.no.slice(0, 2);
        }
    }

    $scope.navToFormQuery = function() {
        $location.path("/form/query/"+schemaTable+"/"+angular.toJson($scope.config));
    }
});