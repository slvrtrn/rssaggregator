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

angular.module('app-auth', ['ui.router', 'restangular', 'ui.bootstrap', 'ui.select']);
angular.module('app-news').controller('NewsCtrl', ['$rootScope', '$scope', 'news', 'Restangular',
    '$modal', '$timeout', 'Utils', 'Broadcast',
    function($rootScope, $scope, news, Restangular, $modal, $timeout, Utils, Broadcast) {

        $scope.refreshNewsFeed = function() {
            $scope.isLoading = true;
            Restangular.all('news').getList().then(function(news) {
                $scope.news = news;
                $scope.isLoading = false;
            });
        };

        $scope.$on("refreshNews", function() {
            $scope.refreshNewsFeed();
        });

        $scope.openNewsItemModal = function (i) {
            var modalInstance = $modal.open({
                templateUrl: '/templates/news/modal/newsItem.html',
                controller: function ($scope, $modalInstance, item, Utils) {
                    $scope.utils = new Utils();
                    $scope.item = item;
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
                controller: function ($scope, $modalInstance, Restangular, Broadcast) {
                    $scope.error = false;
                    $scope.errorMsg = "";
                    $scope.rssUrl = "";
                    $scope.feed = Restangular.all("urls").getList().$object;
                    $scope.alert = {show: false, type: "danger", msg: ""};
                    $scope.addRssUrl = function() {
                        $scope.closeAlert();
                        var newRssUrl = {url: $scope.rssUrl};
                        $scope.feed.post(newRssUrl).then(
                            function(res) {
                                $scope.feed = Restangular.restangularizeCollection(null, res, "urls");
                                $scope.rssUrl = "";
                                $rootScope.$broadcast("refreshNews");
                            },
                            function(failure) {
                                var msg = failure.data[0].userMessage
                                    + " [" + failure.status + " " + failure.statusText + "]";
                                $scope.alert.type = "danger";
                                $scope.alert.msg = msg;
                                $scope.alert.show = true;
                            }
                        );
                    };
                    $scope.removeRssUrl = function(i) {
                        $scope.closeAlert();
                        var item = $scope.feed[i];
                        item.remove().then(function() {
                            var index = $scope.feed.indexOf(item);
                            if (index > -1) $scope.feed.splice(index, 1);
                            $scope.rssUrl = "";
                            $rootScope.$broadcast("refreshNews");
                        });
                    };
                    $scope.closeAlert = function(i) {
                        $scope.alert  = {show: false};
                    };
                    $scope.close = function() {
                        $modalInstance.close();
                    }
                },
                size: 'xs',
                backdrop: true
            });
        };

        $scope.nextPage = function() {
            var id = $scope.news[news.length - 1]._id.$oid;
            $scope.news.one("start", id).getList().then(function(news) {
                Array.prototype.push.apply($scope.news, news)
            });
        };

        $scope.isRefreshing = false;
        $scope.isLoading = false;
        $scope.news = news;
        //$scope.autoRefresh = false;
        $scope.utils = new Utils();
        $scope.broadcast = new Broadcast();
}]);
angular.module('app-auth').controller('AuthCtrl', ['$scope', function($scope) {}]);
var myApp = angular.module('rssaggregator', [
    'ui.router',
    'restangular',
    'ui.bootstrap',
    'ui.select',
    'infinite-scroll',
    'app-auth',
    'app-news'
]).config([
    '$provide',
    '$locationProvider',
    '$stateProvider',
    '$urlRouterProvider',
    'RestangularProvider',
    function($provide, $locationProvider, $stateProvider, $urlRouterProvider, RestangularProvider) {
        RestangularProvider.setRestangularFields({
            id: '_id.$oid'
        });
        RestangularProvider.setBaseUrl('/api/v1');
        $locationProvider.html5Mode(true);
        $urlRouterProvider
            .when('/','/app/news/')
            .otherwise('/app/news');
    }
]).run(['$rootScope', 'Restangular',
    function ($rootScope, Restangular) {
        Restangular.one("whoami").get().then(function(user) {
            $rootScope.currentUser = user;
        });
    }]);
myApp.factory('Utils', function() {
   var Utils = function() {};
    Utils.prototype.formatDate = function(isoDate) {
        return moment(isoDate).format("HH:mm, Do MMMM YYYY")
    };
    Utils.prototype.isDefined = function(obj) {
        return (typeof obj != "undefined");
    };
    return Utils;
});
myApp.factory('Broadcast', ['$rootScope', function($rootScope) {
    var Broadcast = function() {};
    Broadcast.prototype.broadcast = function(msg) {
        $rootScope.$broadcast(msg);
    };
    return Broadcast;
}]);