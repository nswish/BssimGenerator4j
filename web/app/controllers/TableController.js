bssim.controller("TableController", function($scope, $http, $routeParams){
    var schemaTable = $scope.schemaTable = $routeParams['fullTable'];

    var javaCodeCm = CodeMirror(document.getElementById("java_div"), {
        mode : 'clike',
        theme : 'eclipse'
    });
    javaCodeCm.setSize('100%', 'auto');

    var xmlCodeCm = CodeMirror(document.getElementById("xml_div"), {
        mode : 'xml',
        theme : 'eclipse'
    });
    xmlCodeCm.setSize('100%', 'auto');

    var scriptContentCm = CodeMirror(document.getElementById("script_content_div"), {
        lineNumbers: true,
        mode : 'javascript',
        theme : 'eclipse'
    });
    scriptContentCm.setSize('100%', 'auto');

    $scope.showJava = function(){
        $('#code_div > div').hide();
        $('#java_div').show();
        javaCodeCm.refresh();
    };

    $scope.showXml = function(){
        $('#code_div > div').hide();
        $('#xml_div').show();
        xmlCodeCm.refresh();
    };

    $scope.edit = function(){
        $('#script_div').show();
        scriptContentCm.refresh();
    };

    $scope.cancelEdit = function(){
        $('#script_div').hide();
    }

    $scope.save = function(){
        $http.post('tables/'+schemaTable+'/config', {"script_content":scriptContentCm.getValue()}).success(function(result){
            $scope.cancelEdit();
            $scope.generate();
        });
    }

    $scope.generate = function(){
        $http.get('tables/'+schemaTable).success(function(result){
            scriptContentCm.setValue(result['data']['script_content']);
            javaCodeCm.setValue(result['data']['javaCode']);
            xmlCodeCm.setValue(result['data']['xmlCode']);
        });
    };

    $scope.generate();
});