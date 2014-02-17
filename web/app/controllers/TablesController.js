bssim.controller("TablesController", function($scope, Tables, $location, Noty){
    $scope.searchWord = "";
    var path = $location.path();

    Noty.loading('Loading...', path);

    Tables.all().success(function(result){
        if(!result.status){
            Noty.error(result.message, path);
        } else{
            Noty.closeAll();
            $scope.groups = $scope.originGroups = result.data;
        }
    }).error(function(){
        Noty.error("网络异常...", path);
    });

    $scope.$watch("searchWord", function(newValue){
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

    $scope.tableClicked = function(fullTable){
        $scope.currentTable = fullTable;
        $('#myModal').modal('show');
    };

    $scope.navToServiceJspJs = function() {
        $('#myModal').modal('hide');
        $location.path("/form/name/"+$scope.currentTable+"/{}");
    }

    $scope.navToModelSql = function() {
        $('#myModal').modal('hide');
        $location.path("/tables/"+$scope.currentTable);
    }
});
