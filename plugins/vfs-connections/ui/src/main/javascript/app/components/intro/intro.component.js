define([
  'text!./intro.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./intro.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: introController
  };

  introController.$inject = ["$location", "$state", "$stateParams", "dataService"];

  function introController($location, $state, $stateParams, dataService) {
    var vm = this;
    vm.$onInit = onInit;
    vm.next = next;
    vm.canNext = canNext;
    vm.onSelect = onSelect;
    vm.type = null;

    function onInit() {
      vm.newVfsConnection = i18n.get('vfs-connections.intro.label');
      vm.connectionName = i18n.get('vfs-connections.connectionname.label');
      vm.connectionType = "Connection type";
      vm.nextLabel = "Next";
      if ($stateParams.data) {
        vm.data = $stateParams.data;
      } else {
        vm.data = {
          name: "",
          type: ""
        }
      }
      var connection = $location.search().connection;
      dataService.getTypes().then(function(res) {
        vm.connectionTypes = res.data;
        if (vm.data.type) {
          vm.type = vm.data.type.value;
        }
        if (connection) {
          dataService.getConnection(connection).then(function(res) {
            var data = res.data;
            vm.type = data.type;
            vm.data.name = data.model.name;
            vm.data.fields = data.fields;
            vm.data.model = data.model;
            vm.data.template = data.template;
          });
        }
      });
    }

    function onSelect(option) {
      if (option.value !== vm.type) {
        vm.data.fields = null;
        vm.data.template = null;
      }
      vm.data.type = option;
    }

    function next() {
      $state.go("connection-details", {
        data: vm.data
      });
    }

    function canNext() {
      return !vm.data.name || !vm.data.type;
    }
  }

  return {
    name: "intro",
    options: options
  };

});
