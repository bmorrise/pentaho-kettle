define([
  'text!./step1.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./step1.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: step1Controller
  };

  step1Controller.$inject = ["$state", "$stateParams", "$templateCache", "dataService"];

  function step1Controller($state, $stateParams, $templateCache, dataService) {
    var vm = this;
    vm.$onInit = onInit;
    vm.canNext = canNext;

    function onInit() {
      vm.data = $stateParams.data ? $stateParams.data : {};
      vm.connectionDetails = "Connection Details";
      vm.host = "Hostname";
      vm.port = "Port";
      vm.username = "Username";
      vm.password = "Password";

      vm.data.mapping = {
        "host": vm.host,
        "port": vm.port,
        "username": vm.username,
        "password": vm.password
      }
    }

    function canNext() {
      if (vm.data && vm.data.model) {
        return vm.data.model.host;
      }
      return false;
    }
  }

  return {
    name: "otherstep1",
    options: options
  };

});
