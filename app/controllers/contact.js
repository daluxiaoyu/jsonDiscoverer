angular.module("jsonDiscoverer").controller("ContactCtrl", ["$scope", "$window", "$location",
    function($scope, $window, $location) {
        $scope.$on('$viewContentLoaded', function(event) {
            $window.ga('send', 'pageview', {'page': $location.path()});    
        });
    }
]);
