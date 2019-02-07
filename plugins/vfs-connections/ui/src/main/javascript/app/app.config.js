/*!
 * Copyright 2017 Hitachi Vantara. All rights reserved.
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
 * Defines the config for the file open save dialog
 */
define([], function() {
  'use strict';

  config.$inject = ['$stateProvider'];

  /**
   * The config for the file open save app
   *
   * @param {Object} $stateProvider - Controls the state of the app
   */
  function config($stateProvider) {

    $stateProvider
      .state('intro', {
        url: "/intro",
        template: "<intro></intro>",
        params: {
          data: null
        }
      })
      .state('connection-details', {
        url: "/connection-details",
        template: "<connection-details></connection-details>",
        params: {
          data: null
        }
      })
      .state('summary', {
        url: "/summary",
        template: "<summary></summary>",
        params: {
          data: null
        }
      })
      .state('creating', {
        url: "/creating",
        template: "<creating></creating>",
        params: {
          data: null
        }
      })
      .state('final', {
        url: "/final",
        template: "<final></final>",
        params: {
          data: null
        }
      })
  }
  return config;
});
