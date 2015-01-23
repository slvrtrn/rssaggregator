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
        $urlRouterProvider
            .when('/','/app/news/')
            .when('/logout', '/logout')
            .otherwise('/app/news');
    }
]).run(['$rootScope', 'Restangular',
    function ($rootScope, Restangular) {
        $rootScope.parsePubDate = function(isoDate) {
            return moment(isoDate).format("HH:mm, Do MMMM YYYY")
        };
        Restangular.one("whoami").get().then(function(user) {
            $rootScope.currentUser = user;
        });
    }]);