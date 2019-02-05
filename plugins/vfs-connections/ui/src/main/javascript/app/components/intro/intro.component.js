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

  introController.$inject = ["$state"];

  function introController($state) {
    var vm = this;
    vm.$onInit = onInit;
    vm.next = next;

    function onInit() {
      vm.newVfsConnection = i18n.get('vfs-connections.intro.label');
      vm.connectionName = i18n.get('vfs-connections.connectionname.label');
      vm.connectionType = "Connection type";
      vm.nextLabel = "Next";
    }

    function next() {
      $state.go("connection-details");
    }
  }

  return {
    name: "intro",
    options: options
  };

});
