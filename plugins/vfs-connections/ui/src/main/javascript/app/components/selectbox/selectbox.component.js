define([
  'text!./selectbox.html',
  'css!./selectbox.css'
], function (template) {

  'use strict';

  var options = {
    bindings: {
      options: "<",
      type: "<",
      onSelect: "&"
    },
    controllerAs: "vm",
    template: template,
    controller: selectboxController
  };

  selectboxController.$inject = [];

  function selectboxController() {
    var vm = this;
    vm.$onInit = onInit;
    vm.$onChanges = onChanges;
    vm.selectOption = selectOption;
    vm.toggleOptions = toggleOptions;
    vm.isShowOptions = false;

    vm.selectedValue = null;

    function onInit() {
      vm.selectedValue = vm.options && vm.options.length > 0 ? vm.options[0] : null;
    }

    function onChanges(changes) {
      if (changes.type && changes.type.currentValue !== null && vm.options) {
        for (var i = 0; i < vm.options.length; i++) {
          if (vm.options[i].value === changes.type.currentValue) {
            selectOption(vm.options[i]);
          }
        }
      }
    }

    function toggleOptions() {
      vm.isShowOptions = !vm.isShowOptions;
    }

    function selectOption(option) {
      vm.selectedValue = option;
      vm.isShowOptions = false;
      vm.onSelect({value: option});
    }
  }

  return {
    name: "selectbox",
    options: options
  };

});
