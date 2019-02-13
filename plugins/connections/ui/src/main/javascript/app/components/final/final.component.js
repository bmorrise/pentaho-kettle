define([
  'text!./final.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./final.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: finalController
  };

  finalController.$inject = ["$state", "$stateParams"];

  function finalController($state, $stateParams) {
    var vm = this;
    vm.$onInit = onInit;
    vm.onCreateNew = onCreateNew;
    vm.onEditConnection = onEditConnection;

    function onInit() {
      vm.congratulationsLabel = "Congratulations!";
      vm.readyCreate = "Your VFS has been created and is ready to use.";
      vm.question = "What would you like to do?";
      vm.createNewConnection = "Create new VFS connection";
      vm.editConnection = "Edit this connection";
      vm.closeLabel = "Close";

      vm.data = $stateParams.data;
    }

    function onCreateNew() {
      $state.go("intro");
    }

    function onEditConnection() {
      $state.go("intro", {data: vm.data});
    }
  }

  return {
    name: "final",
    options: options
  };

});
