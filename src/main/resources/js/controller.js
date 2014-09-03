function extracted($http) {
    return $http.get("/posts");
}

angular.module('XOverFlow', [])
    .controller('PostController', ['$scope', '$http', function($scope, $http) {


        $scope.postquestion = function(question) {
            $http.put("/post", question);
        }

        $scope.currentpost = {subject:'Question', body:'toto', username:'username', date:'date'};

        $scope.readquestion = function(questionid) {
//           return $http.get("/post" + questionid);
            return {subject:'Question', body:'toto', username:'username', date:'date'};
        }

        $scope.initposts = function() {
            var response = $http.get("/posts");
            response.success(function (data){
                $scope.posts = data;
            })
        }

    }]);