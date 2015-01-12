angular.module('app-auth', ['ui.router', 'restangular', 'ui.bootstrap', 'ui.select']);
angular.module('app-news', ['ui.router', 'restangular', 'ui.bootstrap', 'ui.select'])
    .config(['$stateProvider', function ($stateProvider) {
    'use strict';

    $stateProvider
        .state('news', {
            url: '/app/news',
            abstract: true,
            views: {
                "layout": {
                    templateUrl: '/templates/layout.html'
                }
            }
        })
        .state('news.index', {
            url: '/',
            views: {
                "news": {
                    templateUrl: '/templates/news/news.html',
                    controller: 'NewsCtrl'
                }
            },
            resolve: {
                news: ['Restangular', '$stateParams', function (Restangular, $stateParams) {
                    return Restangular.all('news').getList();
                }]
            }
        });
}]);

angular.module('app-auth').controller('AuthCtrl', ['$scope', function($scope) {}]);
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
angular.module('rssaggregator', [
    'ui.router',
    'restangular',
    'ui.bootstrap',
    'ui.select',
    'app-auth',
    'app-news'
]).config([
    '$provide',
    '$locationProvider',
    '$stateProvider',
    '$urlRouterProvider',
    'RestangularProvider',
    function($provide, $locationProvider, $stateProvider, $urlRouterProvider, RestangularProvider) {
        RestangularProvider.setBaseUrl('/api/v1');
        $locationProvider.html5Mode(true);
        $urlRouterProvider.when('/','/app/news/').otherwise('/app/news');
    }
]).run(['$rootScope', 'Restangular',
    function ($rootScope, Restangular) {
        $rootScope.parsePubDate = function(isoDate) {
            return moment(isoDate).format("Do MMMM YYYY, HH:mm")
        };
        Restangular.one("whoami").get().then(function(user) {
            $rootScope.currentUser = user;
        });
    }]);