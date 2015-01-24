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