angular.module('news', [
    'ui.router',
    'restangular',
    'ui.bootstrap',
    'ui.select',
    'restangular',
    'app-news'
]).config([
    '$provide',
    '$locationProvider',
    '$stateProvider',
    '$urlRouterProvider',
    'RestangularProvider',
    function($provide, $locationProvider, $stateProvider, $urlRouterProvider, RestangularProvider) {
        //RestangularProvider.setBaseUrl('/api/v1');
        //$locationProvider.html5Mode(true);
        //$urlRouterProvider.when('/','/news').otherwise('/news');
    }
]);