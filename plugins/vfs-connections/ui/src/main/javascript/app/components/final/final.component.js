define([
  'text!./final.html',
  'pentaho/i18n-osgi!vfs-connections.messages',
  'css!./final.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {},
    controllerAs: "vm",
    template: template,
    controller: finalController
  };

  finalController.$inject = [];

  function finalController() {
    var vm = this;
    vm.$onInit = onInit;

    function onInit() {

    }
  }

  return {
    name: "final",
    options: options
  };

});
