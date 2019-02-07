define([
  'text!./connection-details.html',
  'text!./temp.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./connection-details.css'
], function (template, temp, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: connectionDetailsController
  };

  connectionDetailsController.$inject = ["$state", "$stateParams", "$templateCache", "dataService"];

  function connectionDetailsController($state, $stateParams, $templateCache, dataService) {
    var vm = this;
    vm.$onInit = onInit;
    vm.back = back;
    vm.next = next;
    vm.getLabel = getLabel;

    function onInit() {
      vm.data = $stateParams.data ? $stateParams.data : {};
      vm.connectionDetails = "Connection Details";
      vm.nextLabel = "Next";
      vm.backLabel = "Back";
      if (vm.data.type && !vm.data.fields) {
        dataService.getFields(vm.data.type.value).then(function(res) {
          vm.data.model = res.data.model;
          vm.data.fields = res.data.fields;
          vm.data.template = res.data.template;
          $templateCache.put('template.html', vm.data.template);
        });
      } else {
        $templateCache.put('template.html', vm.data.template);
      }
    }

    function back() {
      $state.go("intro", {data: vm.data});
    }

    function next() {
      $state.go("summary", {data: vm.data});
    }

    function getLabel(key) {
      if (vm.data && vm.data.fields) {
        for (var i = 0; i < vm.data.fields.length; i++) {
          if (vm.data.fields[i].name === key) {
            return vm.data.fields[i].label;
          }
        }
      }
      return "";
    }
  }

  return {
    name: "connectionDetails",
    options: options
  };

});
