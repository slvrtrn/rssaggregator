angular.module('app-news').controller('NewsCtrl', ['$scope', 'news', 'Restangular', '$modal',
    function($scope, news, Restangular, $modal) {

        $scope.news = news;

        $scope.refreshNewsFeed = function() {
            var n = Restangular.all('news').getList().then(function(news) {
                $scope.news = news;
            });
        };

        $scope.openNewsItemModal = function (i) {
            var modalInstance = $modal.open({
                templateUrl: '/templates/news/modal/newsItem.html',
                controller: function ($scope, $modalInstance, item) {
                    $scope.item = item;
                    $scope.close = function() {
                        return $modalInstance.close();
                    }
                },
                size: 'xs',
                backdrop: true,
                resolve: {
                    item: function () {
                        return $scope.news[i];
                    }
                }
            });
        };

        $scope.openSubscriptionsModal = function () {
            var modalInstance = $modal.open({
                templateUrl: '/templates/news/modal/subscriptions.html',
                controller: function ($scope, $modalInstance) {
                    $scope.addRssUrl = function() {

                    };
                    $scope.removeRssUrl = function() {

                    };
                    $scope.close = function() {
                        return $modalInstance.close();
                    }
                },
                size: 'xs',
                backdrop: true
            });
        }
}]);