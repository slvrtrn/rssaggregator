angular.module('app-news').controller('NewsCtrl', ['$rootScope', '$scope', 'news', 'Restangular',
    '$modal', '$timeout', 'Utils', 'Broadcast',
    function($rootScope, $scope, news, Restangular, $modal, $timeout, Utils, Broadcast) {

        $scope.refreshNewsFeed = function() {
            if($scope.selectedRssId == "all") {
                Restangular.all('news').getList().then(function (news) {
                    $scope.news = news;
                    $scope.isLoading = false;
                });
            } else {
                $scope.isLoading = true;
                Restangular.one('urls', $scope.selectedRssId).getList("news").then(function (news) {
                    $scope.news = news;
                    $scope.isLoading = false;
                });
            }
        };

        $scope.$on("refreshNewsFromModal", function() {
            $scope.selectedRssId = "all";
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
                controller: function ($rootScope, $scope, $modalInstance, Restangular, Broadcast) {
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
                                $rootScope.currentUser.feed = $scope.feed;
                                Broadcast.broadcast("refreshNewsFromModal");
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
                            $rootScope.currentUser.feed = $scope.feed;
                            Broadcast.broadcast("refreshNewsFromModal");
                        });
                    };
                    $scope.closeAlert = function(i) {
                        $scope.alert  = {show: false};
                    };
                    $scope.close = function() {
                        $modalInstance.close();
                    }
                },
                resolve: {
                    "Broadcast": function() {
                        return $scope.broadcast;
                    }
                },
                size: 'xs',
                backdrop: true
            });
        };

        $scope.nextPage = function() {
            var length = $scope.news.length;
            if(length > 0) {
                var id = $scope.news[$scope.news.length - 1]._id.$oid;
                if ($scope.selectedRssId == "all") {
                    $scope.news.getList({"start": id}).then(function(news) {
                        Array.prototype.push.apply($scope.news, news)
                    });
                } else {
                    Restangular.one("urls", $scope.selectedRssId).getList("news", {"start": id}).then(function(news) {
                        Array.prototype.push.apply($scope.news, news)
                    });
                }
            }
        };

        $scope.isRefreshing = false;
        $scope.isLoading = false;
        $scope.news = news;
        $scope.utils = new Utils();
        $scope.broadcast = new Broadcast();
        $scope.selectedRssId = "all";
}]);