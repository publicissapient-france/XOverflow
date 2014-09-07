var app = angular.module('XOverFlow', ['ngRoute', 'ControllersModule']);

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/posts', {
                templateUrl: 'template/posts.html',
                controller: 'PostsController'
            }).
            when('/posts/:query', {
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
    }]
);

var postControllers = angular.module('ControllersModule', []);
postControllers.controller('PostsController', ['$scope', '$http', '$routeParams', '$location',
    function ($scope, $http, $routeParams, $location) {
        $scope.postquestion = function (question) {
            var response = $http.put("/post", question);

            response.success(function (questionResult) {
                $location.path("/post/" + questionResult.id)
            })
        }

        $scope.postanswer = function (answer, questionId) {
            var response = $http.put("/post/" + questionId + "/answer", answer);

            response.success(function (questionResult) {
                $scope.currentpost = questionResult;
            })
        }

        $scope.viewPost = function () {
            var response = $http.get("/post/" + $routeParams.id);
            response.success(function (data) {
                $scope.currentpost = data;
            })
        }

        $scope.initposts = function () {
            if ($routeParams.query != null) {
                var response = $http.get("/search/" + $routeParams.query);
                response.success(function (data) {
                    $scope.posts = data;
                })
            } else {
                var response = $http.get("/posts");
                response.success(function (data) {
                    $scope.posts = data;
                })
            }
        }

        $scope.query = "";
        $scope.search = function (query) {
            $location.path("/posts/" + query);
        }
    }]
);