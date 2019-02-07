define([
  'text!./summary.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./summary.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: summaryController
  };

  summaryController.$inject = ["$state", "$stateParams"];

  function summaryController($state, $stateParams) {
    var vm = this;
    vm.$onInit = onInit;
    vm.next = next;

    function onInit() {
      vm.connectionSummary = "VFS connection summary";
      vm.generalSettings = "General Settings";
      vm.connectionDetails = "Connection details";
      vm.finishLabel = "Finish";
      vm.data = $stateParams.data;
    }

    function next() {
      $state.go("creating", {data: vm.data});
    }
  }

  return {
    name: "summary",
    options: options
  };

});
