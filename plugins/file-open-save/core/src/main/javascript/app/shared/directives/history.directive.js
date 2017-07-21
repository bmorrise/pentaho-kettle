define([
  'angular'
], function (angular) {

  history.$inject = ['$compile', '$document'];

  function history($compile, document) {
    return {
      retrict: 'AE',
      scope: {
        onSearch: '&',
        onBlur: '&',
        onReset: '&',
        searches: '<',
        placeholder: '<',
        selectedFolder: '<',
        value: '=',
      },
      template: '<input id="headerSearchId" class="headerSearch" placeholder="{{placeholder}} {{selectedFolder}}" searches="searches" ng-model="value" ng-change="doSearch(value)" ng-focus="onFocus()" ng-click="onClick($event)"/><ul class="searchHistoryList" ng-show="doShow"><li ng-repeat="search in searches" ng-click="doSearch(search)">{{search}}</li></ul><div class="headerSearchRight" ng-show="value" ng-click="resetSearch()"></div>',
      link: function (scope, element, attr) {
        scope.doShow = false;
        scope.doSearch = function(value) {
          scope.value = value;
          scope.onSearch();
          scope.doShow = false;
        };
        scope.onClick = function(event) {
          event.stopPropagation();
        };
        scope.onFocus = function() {
          if (scope.searches && scope.searches.length > 0) {
            scope.doShow = true;
          }
        };
        scope.resetSearch = function() {
          scope.onReset();
        }
        document.on('click', function(event) {
          scope.onBlur();
          scope.doShow = false;
          scope.$apply();
        })
      }
    }
  }

  return {
    name: "history",
    options: ['$compile', '$document', history]
  }
});
