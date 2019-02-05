define([
  'text!./connection-details.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./connection-details.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: connectionDetailsController
  };

  connectionDetailsController.$inject = ["$state"];

  function connectionDetailsController($state) {
    var vm = this;
    vm.$onInit = onInit;
    vm.back = back;
    vm.next = next;
    vm.getData = getData;

    function onInit() {
      vm.connectionDetails = "Connection Details";
      vm.serviceAccountKey = "Service Account Key";
      vm.nextLabel = "Next";
      vm.backLabel = "Back";

      vm.fields = [
        {
          type: "text",
          label: "Service Account Key",
          dataField: "serviceAccountKey"
        }
      ];

      vm.data = {
        serviceAccountKey: "Testing"
      }
    }

    function back() {
      $state.go("intro");
    }

    function next() {
      console.log(vm.data);
      $state.go("summary");
    }

    function getData(key) {
      return vm.data[key];
    }
  }

  return {
    name: "connectionDetails",
    options: options
  };

});
