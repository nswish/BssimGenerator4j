bssim.controller("NavbarController", function($scope){
    $scope.$on('$routeChangeSuccess', function(event, newRoute){
        console.log(arguments);
        if(newRoute.$$route.originalPath == "/") {
            $scope.selected_tabname = '首页';
        } else if(newRoute.$$route.originalPath == "/config") {
            $scope.selected_tabname = '设置';
        }
    });
})
