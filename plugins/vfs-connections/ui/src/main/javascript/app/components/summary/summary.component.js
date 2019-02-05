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

  summaryController.$inject = ["$state"];

  function summaryController() {
    var vm = this;
    vm.$onInit = onInit;
    vm.next = next;

    function onInit() {
      vm.connectionSummary = "VFS connection summary";
      vm.finishLabel = "Finish";
    }

    function next() {
      $state.go("creating");
    }
  }

  return {
    name: "summary",
    options: options
  };

});
