angular.module('XOverFlow', [])
    .controller('PostController', ['$scope', function($scope) {
        $scope.posts = [
            {subject:'Question 1 ?'},
            {subject:'Question 2 ?'},
            {subject:'Question 3 ?'},
            {subject:'Question 4 ?'},
            {subject:'Question 5 ?'}];

    }]);