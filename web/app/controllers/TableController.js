bssim.controller("TableController", function($scope, $http, $routeParams, Noty, $location){
    var path = $location.path();
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

    $scope.commit = function() {
        Noty.loading('正在提交...', path);
        $http.post('tables/'+schemaTable+'/commit').success(function(result){
            if(result.status){
                Noty.success(result.message, path, 4000, {
                    template: '<div class="noty_message"><pre class="noty_text" style="text-align: left; background-color: transparent; border: 0; margin: 0;"></pre><div class="noty_close"></div></div>'
                });
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(){
            Noty.error('网络异常...', path);
        });
    }

    $scope.save = function(){
        $http.post('tables/'+schemaTable+'/config', {"script_content":scriptContentCm.getValue()}).success(function(result){
            if(result.status) {
                Noty.success(result.message, path);
                $scope.cancelEdit();
                $scope.generate();
            } else {
                Noty.error(result.message, path);
            }
        }).error(function(){
            Noty.error('网络异常...', path);
        });
    }

    $scope.generate = function(){
        Noty.loading('Loading...', path);
        $http.get('tables/'+schemaTable).success(function(result){
            if(result.status){
                Noty.closeAll();
                scriptContentCm.setValue(result['data']['script_content']);
                javaCodeCm.setValue(result['data']['javaCode']);
                xmlCodeCm.setValue(result['data']['xmlCode']);
            } else {
                Noty.error(result.message, path);
                scriptContentCm.setValue('error');
                javaCodeCm.setValue('error');
                xmlCodeCm.setValue('error');
            }
        }).error(function(){
            Noty.error('网络异常...', path);
        });
    };

    $scope.generate();
});