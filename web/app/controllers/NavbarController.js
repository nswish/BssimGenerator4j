bssim.controller("NavbarController", function($scope){
    $scope.$on('$routeChangeSuccess', function(event, newRoute){
        if(newRoute.$$route.originalPath == "/" || newRoute.$$route.originalPath.indexOf("/tables/") > -1) {
            $scope.selected_tabname = '首页';
        } else if(newRoute.$$route.originalPath == "/config") {
            $scope.selected_tabname = '设置';
        } else if(newRoute.$$route.originalPath.indexOf("/docs/") > -1) {
            $scope.selected_tabname = '文档';
        }
    });
})
