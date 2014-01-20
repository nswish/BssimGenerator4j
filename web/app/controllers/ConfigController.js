bssim.controller("ConfigController", function($scope, Config){
    Config.get().success(function(result){
        $scope.config = result.data;
    });

    $scope.save = function() {
        Config.save($scope.config).success(function(result){
            Config.get().success(function(result){
                $scope.config = result.data;
            });
        });
    }
});