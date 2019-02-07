/*!
 * HITACHI VANTARA PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2017 Hitachi Vantara. All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Hitachi Vantara and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Hitachi Vantara and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Hitachi Vantara is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Hitachi Vantara,
 * explicitly covering such access.
 */

/**
 * The File Open and Save Main Module.
 *
 * The main module used for supporting the file open and save functionality.
 **/
define([
  "angular",
  "./app.config",
  "./app.animation",
  "./components/intro/intro.component",
  "./components/connection-details/connection-details.component",
  "./components/summary/summary.component",
  "./components/creating/creating.component",
  "./components/final/final.component",
  "./components/selectbox/selectbox.component",
  "./directives/focus.directive",
  "./service/helper.service",
  "./service/data.service",
  "angular-ui-router",
  "angular-animate"
], function(angular, appConfig, appAnimation, introComponent, connectionDetailsComponent, summaryComponent, creatingComponent, finalComponent, selectboxComponent, focusDirective, helperService, dataService) {
  "use strict";

  var module = {
    name: "vfs-connections",
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
    angular.module(module.name, ["ui.router", "ngAnimate"])
      .component(introComponent.name, introComponent.options)
      .component(connectionDetailsComponent.name, connectionDetailsComponent.options)
      .component(summaryComponent.name, summaryComponent.options)
      .component(creatingComponent.name, creatingComponent.options)
      .component(finalComponent.name, finalComponent.options)
      .component(selectboxComponent.name, selectboxComponent.options)
      .directive(focusDirective.name, focusDirective.options)
      .service(helperService.name, helperService.factory)
      .service(dataService.name, dataService.factory)
      .animation(appAnimation.class, appAnimation.factory)
      .config(appConfig);
  }

  /**
   * Bootstraps angular module to the DOM element on the page
   * @private
   * @param {DOMElement} element - The DOM element
   */
  function bootstrap(element) {
    angular.element(element).ready(function() {
      angular.bootstrap(element, [module.name], {
        strictDi: true
      });
    });
  }
});
