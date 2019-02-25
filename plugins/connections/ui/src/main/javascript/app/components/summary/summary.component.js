define([
  'text!./summary.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./summary.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: summaryController
  };

  summaryController.$inject = ["$state", "$stateParams", "vfsTypes"];

  function summaryController($state, $stateParams, vfsTypes) {
    var vm = this;
    vm.$onInit = onInit;
    vm.getLabel = getLabel;
    vm.onEditClick = onEditClick;

    function onInit() {
      vm.connectionSummary = "VFS connection summary";
      vm.generalSettings = "General Settings";
      vm.connectionDetails = "Connection details";
      vm.description = "Description";
      vm.finishLabel = "Finish";
      vm.data = $stateParams.data;
    }

    function getLabel(scheme) {
      for (var i = 0; i < vfsTypes.length; i++) {
        if (vfsTypes[i].value === scheme) {
          return vfsTypes[i].label;
        }
      }
      return "";
    }

    function onEditClick(destination) {
      $state.go(destination, {data: vm.data, transition: "slideRight"});
    }
  }

  return {
    name: "summary",
    options: options
  };

});
