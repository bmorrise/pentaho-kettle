define([
  'text!./controls.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./controls.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {
      back: "<",
      next: "<",
      finish: "<",
      close: "<",
      data: "<",
      canNext: "<"
    },
    controllerAs: "vm",
    template: template,
    controller: controlsController
  };

  controlsController.$inject = ["$state"];

  function controlsController($state) {
    var vm = this;
    vm.onBack = onBack;
    vm.onNext = onNext;
    vm.onFinish = onFinish;
    vm.onClose = onClose;
    vm.backLabel = "Back";
    vm.nextLabel = "Next";
    vm.finishLabel = "Finish";
    vm.closeLabel = "Close";
    vm.$onInit = onInit;

    function onInit() {
    }

    function onBack() {
      $state.go(vm.back, {data: vm.data, transition: "slideRight"});
    }

    function onNext() {
      $state.go(vm.next, {data: vm.data, transition: "slideLeft"});
    }

    function onFinish() {
      $state.go(vm.finish, {data: vm.data, transition: "slideLeft"});
    }

    function onClose() {
      close();
    }
  }

  return {
    name: "controls",
    options: options
  };

});
