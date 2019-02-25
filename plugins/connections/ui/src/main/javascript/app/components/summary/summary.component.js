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
      vm.connectionName = i18n.get('connections.intro.connectionName');
      vm.connectionType = i18n.get('connections.intro.connectionType');
      vm.connectionSummary = i18n.get('connections.summary.connectionSummary');
      vm.generalSettings = i18n.get('connections.summary.generalSettings');
      vm.connectionDetails = i18n.get('connections.summary.connectionDetails');
      vm.description = i18n.get('connections.summary.description');
      vm.finishLabel = i18n.get('connections.summary.finishLabel');
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
