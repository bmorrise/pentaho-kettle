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
          provider: "vfs",
          selectFolder: selectFolder,
          getPath: getPath,
          createFolder: createFolder,
          findRootNode: findRootNode,
          parsePath: parsePath
        };

        function findRootNode(tree, folder, path) {
          for (var i = 0; i < tree.length; i++) {
            if (tree[i].name.toLowerCase() === folder.root.toLowerCase()) {
              var node = tree[i];
              if (path.indexOf("/") !== -1) {
                node = _findConnection(node, folder.connection);
              }
              return node;
            }
          }
          return null;
        }

        function parsePath(path, folder) {
          if (path.indexOf(folder.root) === 0) {
            path = path.replace(folder.root, "");
          }
          if (path.indexOf("/") === 0) {
            path = path.substr(1, path.length);
          }
          if (path.indexOf(folder.connection) === 0) {
            path = path.replace(folder.connection, "");
          }
          if (path.indexOf("/") === 0) {
            path = path.substr(1, path.length);
          }
          return !path ? null : path.split("/");
        }

        function _findConnection(node, connection) {
          if (!connection) {
            return node;
          }
          for ( var i = 0; i < node.children.length; i++ ) {
            if (node.children[i].name.toLowerCase() === connection.toLowerCase()) {
              node.open = true;
              return node.children[i];
            }
          }
        }

        function selectFolder(folder, filters, callback) {
          if (folder.path && !folder.loaded) {
            getFiles(folder.provider, folder.connection, folder.path, filters).then(function(response) {
              folder.children = response.data;
              folder.loaded = true;
              for (var i = 0; i < folder.children.length; i++) {
                folder.children[i].connection = folder.connection;
                folder.children[i].provider = folder.provider;
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

        function getPath(folder) {
          if (!folder.path) {
            return folder.root + "/" + folder.name;
          }
          return folder.root + "/" + ( folder.connection ? folder.connection + "/" : "" ) + folder.path.replace(/^[a-z]+:\/\//, "");
        }

        function createFolder(node, name) {
          return {name: name, connection: node.connection, path: node.path + "/" + name, children: [], open: true, type: "folder", provider: node.provider, root: node.root};
        }

        /**
         * Gets the directory tree for the currently connected repository
         *
         * @return {Promise} - a promise resolved once data is returned
         */
        function getFiles(type, connection, path, filters) {
          return helperService.httpGet([baseUrl, "getFiles"].join("/") + "?type="+type+"&connection=" + connection + "&path=" + path + (filters?"&filters="+filters:"") );
        }
      }
    });
