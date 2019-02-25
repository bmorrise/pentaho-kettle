/*!
 * Copyright 2019 Hitachi Vantara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

define([
  'text!./controls.html',
  'pentaho/i18n-osgi!connections.messages',
  'css!./controls.css'
], function (template, i18n) {

  'use strict';

  var options = {
    bindings: {
      test: "<",
      back: "<",
      next: "<",
      finish: "<",
      close: "<",
      data: "<",
      canNext: "<",
      onTest: "&",
      nextValidation: "<"
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
    vm.onTestClick = onTestClick;
    vm.backLabel = i18n.get('connections.controls.backLabel');
    vm.nextLabel = i18n.get('connections.controls.nextLabel');
    vm.finishLabel = i18n.get('connections.controls.finishLabel');
    vm.closeLabel = i18n.get('connections.controls.closeLabel');
    vm.testLabel = i18n.get('connections.controls.testLabel');
    vm.$onInit = onInit;

    function onInit() {
    }

    function onBack() {
      $state.go(vm.back, {data: vm.data, transition: "slideRight"});
    }

    function onNext() {
      if (vm.nextValidation) {
        vm.nextValidation(function(isValid) {
          if (isValid) {
            $state.go(vm.next, {data: vm.data, transition: "slideLeft"});
          }
        });
      } else {
        $state.go(vm.next, {data: vm.data, transition: "slideLeft"});
      }
    }

    function onFinish() {
      $state.go(vm.finish, {data: vm.data, transition: "slideLeft"});
    }

    function onClose() {
      close();
    }

    function onTestClick() {
      vm.onTest();
    }
  }

  return {
    name: "controls",
    options: options
  };

});
