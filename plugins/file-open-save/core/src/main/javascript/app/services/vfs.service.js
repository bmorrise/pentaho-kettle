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
          addFolder: addFolder,
          findRootNode: findRootNode,
          parsePath: parsePath,
          deleteFiles: deleteFiles,
          renameFile: renameFile,
          isCopy: isCopy,
          getCopyFromParameters: getCopyFromParameters,
          getCopyToParameters: getCopyToParameters
        };

        function findRootNode(tree, folder, path) {
          path = _stripProtocol(path);
          for (var i = 0; i < tree.length; i++) {
            if (tree[i].provider.toLowerCase() === folder.provider.toLowerCase()) {
              var node = tree[i];
              node.open = true;
              if (path.indexOf("/") !== -1) {
                node = _findConnection(node, folder.connection);
              }
              return node;
            }
          }
          return null;
        }

        function parsePath(path, folder) {
          var newPath = _stripProtocol(path);
          if (newPath.indexOf(folder.root) === 0) {
            newPath = newPath.replace(folder.root, "");
          }
          if (newPath.indexOf("/") === 0) {
            newPath = newPath.substr(1, newPath.length);
          }
          if (newPath.indexOf(folder.connection) === 0) {
            newPath = newPath.replace(folder.connection, "");
          }
          if (newPath.indexOf("/") === 0) {
            newPath = newPath.substr(1, newPath.length);
          }
          return !newPath ? null : newPath.split("/");
        }

        function _findConnection(node, connection) {
          if (!connection) {
            return node;
          }
          for ( var i = 0; i < node.children.length; i++ ) {
            if (node.children[i].name.toLowerCase() === connection.toLowerCase()) {
              node.children[i].open = true;
              return node.children[i];
            }
          }
        }

        function selectFolder(folder, filters) {
          return $q(function(resolve, reject) {
            if (folder.path && !folder.loaded) {
              getFiles(folder, filters).then(function(response) {
                folder.children = response.data;
                folder.loaded = true;
                for (var i = 0; i < folder.children.length; i++) {
                  folder.children[i].connection = folder.connection;
                  folder.children[i].provider = folder.provider;
                }
                resolve();
              });
            } else {
              resolve();
            }
          });
        }

        function getPath(folder) {
          if (!folder.path) {
            return folder.root + "/" + folder.name;
          }
          return folder.root + "/" + ( folder.connection ? folder.connection + "/" : "" ) + folder.path.replace(/^[a-z]+:\/\//, "");
        }

        function _stripProtocol(path) {
          return path.replace(/^[a-z]+:\/\//, "/");
        }

        function createFolder(node, name) {
          return {name: name, connection: node.connection, path: node.path + "/" + name, children: [], open: true, type: "folder", provider: node.provider, root: node.root};
        }

        /**
         * Gets the directory tree for the currently connected repository
         *
         * @return {Promise} - a promise resolved once data is returned
         */
        function getFiles(folder, filters) {
          return helperService.httpGet([baseUrl, "getFiles"].join("/") + "?provider="+folder.provider+"&connection="+folder.connection+"&path="+folder.path+(filters?"&filters="+filters:"") );
        }

        function addFolder(folder) {
          return helperService.httpPut([baseUrl, "addFolder"].join("/") + "?provider="+folder.provider+"&connection="+folder.connection+"&path="+folder.path)
        }

        function deleteFiles(files) {
          var paths = [];
          for (var i = 0; i < files.length; i++) {
            paths.push(files[i].path);
          }
          var connection = files[0].connection;
          return helperService.httpPost([baseUrl, "delete"].join("/") + "?provider=vfs&connection="+connection, paths);
        }

        function renameFile(file, newPath) {
          return helperService.httpPost([baseUrl, "renameFile"].join("/") + "?provider=vfs&connection="+file.connection+"&path="+file.path+"&newPath="+newPath)
        }

        function isCopy(from, to) {
          return from.provider !== to.provider || from.connection !== to.connection;
        }

        function getCopyFromParameters(from) {
          return {
            fromConnection: from.connection
          };
        }

        function getCopyToParameters(to) {
          return {
            toConnection: to.connection
          };
        }
      }
    });
