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
