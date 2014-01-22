bssim.controller("DocController", function($scope, $http, Noty, $location, $routeParams){
    var path = $location.path();

    $http.get('docs/'+$routeParams.docName).success(function(result){
        if(result.status){
            console.log(result.data);
            $scope.docContent = result.data;
        } else {
            Noty.error(result.message, path);
        }
    }).error(function(message){
        Noty.error(result.message, path);
    });
});