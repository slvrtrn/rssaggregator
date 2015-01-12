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
angular.module('app-news').controller('NewsCtrl', ['$rootScope', '$scope', 'news', 'Restangular', '$modal', '$timeout',
    function($rootScope, $scope, news, Restangular, $modal, $timeout) {

        //$scope.startTimeout = function() {
        //    //console.log("Setting up refresh news timeout at " + moment().format("HH:mm:ss"));
        //    $scope.timeout = $timeout(function() {
        //        //console.log("Refreshing news at " + moment().format("HH:mm:ss"));
        //        $rootScope.$broadcast("refreshNews");
        //    }, 3000);
        //};
        //
        //$scope.stopTimeout = function() {
        //    $timeout.cancel($scope.timeout);
        //};
        //
        //$scope.toggleTimeout = function() {
        //    $scope.autoRefresh === true ? $scope.startTimeout() : $scope.stopTimeout();
        //};

        $scope.refreshNewsFeed = function() {
            //$rootScope.$broadcast("stopTimeout");
            $scope.isLoading = true;
            var n = Restangular.all('news').getList().then(function(news) {
                $scope.news = news;
                $scope.isLoading = false;
                //$rootScope.$broadcast("startTimeout");
            });
        };

        $scope.$on("refreshNews", function() {
            $scope.refreshNewsFeed();
        });

        //$scope.$on("startTimeout", function() {
        //    if($scope.autoRefresh === true) $scope.startTimeout();
        //});
        //
        //$scope.$on("stopTimeout", function() {
        //    //console.log("Stopping refresh news timeout at " + moment().format("HH:mm:ss"));
        //    if($scope.autoRefresh === true) $scope.stopTimeout();
        //});

        $scope.openNewsItemModal = function (i) {
            var modalInstance = $modal.open({
                templateUrl: '/templates/news/modal/newsItem.html',
                controller: function ($scope, $modalInstance, item) {
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
                controller: function ($scope, $rootScope, $modalInstance, Restangular) {
                    //$rootScope.$broadcast("stopTimeout");
                    $scope.error = false;
                    $scope.errorMsg = "";
                    $scope.rssUrl = "";
                    $scope.feed = Restangular.all("urls").getList().$object;
                    $scope.alert = {show: false, type: "danger", msg: ""};
                    $scope.addRssUrl = function() {
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
                        //$rootScope.$broadcast("startTimeout");
                        $modalInstance.close();
                    }
                },
                size: 'xs',
                backdrop: true
            });
        };

        $scope.isDefined = function(obj) {
            return (typeof obj != "undefined");
        };

        $scope.isLoading = false;
        $scope.news = news;
        //$scope.autoRefresh = false;
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
        RestangularProvider.setRestangularFields({
            id: '_id.$oid'
        });
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