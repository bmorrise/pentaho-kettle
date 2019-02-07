define([
  'text!./creating.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./creating.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: creatingController
  };

  creatingController.$inject = ["$state", "$timeout", "$stateParams", "dataService"];

  function creatingController($state, $timeout, $stateParams, dataService) {
    var vm = this;
    vm.$onInit = onInit;
    vm.data = $stateParams.data;

    function onInit() {
      vm.almostDone = "Almost done";
      vm.message = "Creating your new VFS connection...";

      vm.data.model.name = vm.data.name;
      dataService.createConnection(vm.data.model).then(function() {
        $timeout(function() {
          $state.go("final", {data: vm.data});
        }, 1000);
      });
    }
  }

  return {
    name: "creating",
    options: options
  };

});
