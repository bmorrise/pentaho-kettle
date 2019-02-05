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

  creatingController.$inject = [];

  function creatingController() {
    var vm = this;
    vm.$onInit = onInit;

    function onInit() {
      vm.almostDone = "Almost done";
      vm.message = "Creating your new VFS connection...";
    }
  }

  return {
    name: "creating",
    options: options
  };

});
