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
      vm.connectionName = i18n.get('connections.connectionname.label');
      vm.connectionType = "Connection type";
      vm.connectionDescription = "Description";
      vm.nextLabel = "Next";
      vm.next = "/";

      if ($stateParams.data) {
        vm.title = "Edit VFS Connection";
        vm.data = $stateParams.data;
        vm.type = vm.data.model.type;
        vm.next = vm.data.model.type + "step1";
      } else {
        vm.title = i18n.get('connections.intro.label');
        vm.data = {
          model: null
        }
      }
      var connection = $location.search().connection;
      vm.connectionTypes = vfsTypes;
      if (vm.data.type) {
        vm.type = vm.data.type.value;
      }
      if (connection) {
        dataService.getConnection(connection).then(function (res) {
          var model = res.data;
          vm.type = model.type;
          vm.data.model = model;
          vm.next = vm.data.model.type + "step1";
        });
      }
    }

    function onSelect(option) {
      if (!vm.data.model || vm.data.model.type !== option.value) {
        dataService.getFields(option.value).then(function (res) {
          var name = vm.data.model.name;
          var description = vm.data.model.description;
          vm.data.model = res.data;
          vm.data.model.name = name;
          vm.data.model.description = description;
          vm.next = vm.data.model.type + "step1";
        });
      }
    }

    function canNext() {
      return vm.data.model && vm.data.model.type && vm.data.model.name;
    }
  }

  return {
    name: "intro",
    options: options
  };

});
