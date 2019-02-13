define([
  'text!./intro.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./intro.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: introController
  };

  introController.$inject = ["$location", "$state", "$stateParams", "dataService", "vfsTypes"];

  function introController($location, $state, $stateParams, dataService, vfsTypes) {
    var vm = this;
    vm.$onInit = onInit;
    vm.canNext = canNext;
    vm.onSelect = onSelect;
    vm.type = null;

    function onInit() {
      vm.newVfsConnection = i18n.get('connections.intro.label');
      vm.connectionName = i18n.get('connections.connectionname.label');
      vm.connectionType = "Connection type";
      vm.nextLabel = "Next";
      vm.next = "/";

      // vm.errorMessage = {
      //   message: "An error has occurred"
      // };

      if ($stateParams.data) {
        vm.data = $stateParams.data;
        vm.type = vm.data.model.type;
        vm.next = vm.data.model.type+"step1";
      } else {
        vm.data = {
          name: null
        }
      }
      var connection = $location.search().connection;
      vm.connectionTypes = vfsTypes;
      if (vm.data.type) {
        vm.type = vm.data.type.value;
      }
      if (connection) {
        dataService.getConnection(connection).then(function(res) {
          var model = res.data;
          vm.type = model.type;
          vm.data.name = model.name;
          vm.data.model = model;
        });
      }
    }

    function onSelect(option) {
      if (!vm.data.model || vm.data.model.type !== option.value) {
        dataService.getFields(option.value).then(function(res) {
          vm.data.model = res.data;
          vm.next = vm.data.model.type+"step1";
        });
      }
    }

    function canNext() {
      return vm.data.name && vm.data.model;
    }
  }

  return {
    name: "intro",
    options: options
  };

});
