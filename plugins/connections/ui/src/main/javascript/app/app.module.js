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

/**
 * The Connections Main Module.
 *
 * The main module used for supporting the connections functionality.
 **/
define([
  "angular",
  "pentaho/module/instancesOf!IPenConnectionProvider",
  "./app.config",
  "./app.animation",
  "./components/intro/intro.component",
  "./components/summary/summary.component",
  "./components/creating/creating.component",
  "./components/final/final.component",
  "./components/selectbox/selectbox.component",
  "./components/controls/controls.component",
  "./components/error/error.component",
  "./components/help/help.component",
  "./directives/focus.directive",
  "./service/helper.service",
  "./service/data.service",
  "angular-ui-router",
  "angular-animate"
], function (angular, plugins, appConfig, appAnimation, introComponent, summaryComponent, creatingComponent, finalComponent, selectboxComponent, controlsComponent, errorComponent, helpComponent, focusDirective, helperService, dataService) {
  "use strict";

  var module = {
    name: "connections",
    bootstrap: bootstrap
  };

  activate();

  return module;

  /**
   * Creates angular module with dependencies.
   *
   * @private
   */
  function activate() {

    var deps = ['ui.router', 'ngAnimate'];
    var types = [];
    plugins.map(function (item) {
      deps.push(item.name);
      types.push({
        value: item.scheme,
        label: item.label
      })
    });

    function vfsTypeProvider() {
      function getTypes() {
        return types;
      }

      return {
        $get: getTypes
      }
    }

    angular.module(module.name, deps)
        .component(introComponent.name, introComponent.options)
        .component(summaryComponent.name, summaryComponent.options)
        .component(creatingComponent.name, creatingComponent.options)
        .component(finalComponent.name, finalComponent.options)
        .component(selectboxComponent.name, selectboxComponent.options)
        .component(controlsComponent.name, controlsComponent.options)
        .component(errorComponent.name, errorComponent.options)
        .component(helpComponent.name, helpComponent.options)
        .directive(focusDirective.name, focusDirective.options)
        .service(helperService.name, helperService.factory)
        .service(dataService.name, dataService.factory)
        .animation(appAnimation.class, appAnimation.factory)
        .provider('vfsTypes', vfsTypeProvider)
        .config(appConfig);
  }

  /**
   * Bootstraps angular module to the DOM element on the page
   * @private
   * @param {DOMElement} element - The DOM element
   */
  function bootstrap(element) {
    angular.element(element).ready(function () {
      angular.bootstrap(element, [module.name], {
        strictDi: true
      });
    });
  }
});
