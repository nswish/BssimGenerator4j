bssim.controller("TablesController", function($scope, Tables, $location){
    $scope.searchWord = "";
    $scope.loading = true;
    $scope.loadingText = "Loading...";

    Tables.all().success(function(result){
        if(!result.status){
            $scope.loadingText = result.message;
        } else{
            $scope.groups = $scope.originGroups = result.data;
            $scope.loading = false;
        }
    }).error(function(){
        $scope.loadingText = "网络异常...";
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
        console.log(fullTable);
        $location.path("/tables/"+fullTable);
    };
});
