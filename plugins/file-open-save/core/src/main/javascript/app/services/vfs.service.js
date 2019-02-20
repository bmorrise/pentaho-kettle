/*!
 * Copyright 2017-2018 Hitachi Vantara. All rights reserved.
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
 * The Data Service
 *
 * The Data Service, a collection of endpoints used by the application
 *
 * @module services/data.service
 * @property {String} name The name of the module.
 */
define(
    [],
    function() {
      "use strict";

      var factoryArray = ["helperService", "$http", "$q", factory];
      var module = {
        name: "vfsService",
        factory: factoryArray
      };

      return module;

      /**
       * The dataService factory
       *
       * @param {Object} $http - The $http angular helper service
       *
       * @return {Object} The dataService api
       */
      function factory(helperService, $http, $q) {
        var baseUrl = "/cxf/browser";
        return {
          selectFolder: selectFolder
        };

        function selectFolder(folder, callback) {
          if (folder.path !== null && !folder.loaded) {
            getFiles(folder.action, folder.connection, folder.path).then(function(response) {
              folder.children = response.data;
              folder.loaded = true;
              for (var i = 0; i < folder.children.length; i++) {
                folder.children[i].connection = folder.connection;
                folder.children[i].action = folder.action;
              }
              if (callback) {
                callback();
              }
            });
          } else {
            if (callback) {
              callback();
            }
          }
        }

        /**
         * Gets the directory tree for the currently connected repository
         *
         * @return {Promise} - a promise resolved once data is returned
         */
        function getFiles(type, connection, path) {
          return helperService.httpGet([baseUrl, "getFiles"].join("/") + "?type="+type+"&connection=" + connection + "&path=" + path);
        }
      }
    });
