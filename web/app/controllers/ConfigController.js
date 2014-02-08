bssim.controller("ConfigController", function($scope, Config, $http, Noty, $location){
    var path = $location.path();
    $scope.tableAdding = {"user":"", "table":""};

    var query = function(){
        Config.get().success(function(result){
            if(result.status) {
                $scope.config = result.data;
                $scope.svnConfig = {
                    "svn_repo_path": result.data.svn_repo_path,
                    "svn_debug": result.data.svn_debug
                };
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(message){
            Noty.error(message, path);
        });
    };

    query();

    $scope.save = function() {
        Config.save($scope.config).success(function(result){
            if(result.status){
                Noty.success(result.message, path);
                query();
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(message){
            Noty.error(message, path);
        });
    };

    $scope.saveSvn = function() {
        Config.save($scope.svnConfig).success(function(result){
            if(result.status){
                Noty.success(result.message, path);
                query();
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(message){
            Noty.error(message, path);
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

    $scope['delete'] = function(fullTableName) {
        if(confirm('删除 ['+fullTableName+'] 么?')) {
            $http['delete']('/config/tables/'+fullTableName).success(function(result){
                $scope.config = result.data;
            });
        }
    };
});