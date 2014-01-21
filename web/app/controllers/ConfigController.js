bssim.controller("ConfigController", function($scope, Config, $http){
    $scope.tableAdding = {"user":"", "table":""};

    var query = function(){
        Config.get().success(function(result){
            $scope.config = result.data;
        });
    };

    query();

    $scope.save = function() {
        Config.save($scope.config).success(function(){
            query();
        });
    };

    $scope.add = function(){
        $http.post('/config/tables', $scope.tableAdding).success(function(result){
            if (result.status){
                $('#myModal').modal('hide');
                $scope.tableAdding.user = "";
                $scope.tableAdding.table = "";
                $scope.config = result.data;
            }
        });
    };

    $scope.delete = function(fullTableName) {
        if(confirm('删除 ['+fullTableName+'] 么?')) {
            $http.delete('/config/tables/'+fullTableName).success(function(result){
                $scope.config = result.data;
            });
        }
    };
});