function extracted($http) {
    return $http.get("/posts");
}

var app = angular.module('XOverFlow', ['ngRoute', 'ControllersModule']);

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/posts', {
                templateUrl: 'template/posts.html',
                controller: 'PostsController'
            }).
            when('/post/edit', {
                templateUrl: 'template/postForm.html',
                controller: 'PostsController'
            }).
            when('/post/:id', {
                templateUrl: 'template/postView.html',
                controller: 'PostsController'
            }).
            otherwise({
                redirectTo: '/posts'
            });
    }]);


var postControllers = angular.module('ControllersModule', []);

postControllers.controller('PostsController', ['$scope', '$http', '$routeParams',  '$location', function($scope, $http, $routeParams, $location) {


        $scope.postquestion = function(question) {
            var response = $http.put("/post", question);

            response.success(function (data){
                $location.path("/post/" + data.id)
            })
        }

        $scope.viewPost = function(){
            console.log('ICI');
            var response = $http.get("/post/"+$routeParams.id);
            response.success(function (data){
                $scope.currentpost = data;
            })
        }


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