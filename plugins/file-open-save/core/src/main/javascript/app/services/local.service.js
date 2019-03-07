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

//TODO: Make it work with Windows
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
    function(fileService) {
      "use strict";

      var factoryArray = ["helperService", "$http", "$q", factory];
      var module = {
        name: "localService",
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
          provider: "local",
          selectFolder: selectFolder,
          getPath: getPath,
          addFolder: addFolder,
          createFolder: createFolder,
          findRootNode: findRootNode,
          parsePath: parsePath,
          deleteFiles: deleteFiles,
          renameFile: renameFile,
          isCopy: isCopy,
          getCopyFromParameters: getCopyFromParameters,
          getCopyToParameters: getCopyToParameters
        };

        function findRootNode(tree, folder, path) {
          for (var i = 0; i < tree.length; i++) {
            if (tree[i].provider.toLowerCase() === folder.provider.toLowerCase()) {
              return tree[i];
            }
          }
          return null;
        }

        function parsePath(path, folder) {
          var newPath = path.replace(folder.root, "");
          if (newPath.indexOf("/") === 0) {
            newPath = newPath.substr(1, newPath.length);
          }
          return !newPath ? null : newPath.split("/");
        }

        function selectFolder(folder, filters) {
          return $q(function(resolve, reject) {
            if (folder.path && !folder.loaded) {
              getFiles(folder, filters).then(function(response) {
                folder.children = response.data;
                folder.loaded = true;
                for (var i = 0; i < folder.children.length; i++) {
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
            return folder.root;
          }
          return folder.root + folder.path;
        }

        function createFolder(node, name) {
          return {name: name, path: node.path + "/" + name, children: [], canAddChildren: true, canEdit:true, open: true, type: "folder", provider: node.provider, root: node.root};
        }

        /**
         * Gets the directory tree for the currently connected repository
         *
         * @return {Promise} - a promise resolved once data is returned
         */
        function getFiles(file, filters) {
          return helperService.httpGet([baseUrl, "getFiles"].join("/") + "?provider="+file.provider+"&path="+file.path+(filters?"&filters="+filters:""));
        }

        function deleteFiles(files) {
          var paths = [];
          for (var i = 0; i < files.length; i++) {
            paths.push(files[i].path);
          }
          return helperService.httpPost([baseUrl, "delete"].join("/") + "?provider=local", paths);
        }

        function addFolder(folder) {
          return helperService.httpPut([baseUrl, "addFolder"].join("/") + "?provider="+folder.provider+"&path="+folder.path);
        }

        function renameFile(file, newPath) {
          return helperService.httpPost([baseUrl, "renameFile"].join("/") + "?provider="+file.provider+"&path="+file.path+"&newPath="+newPath);
        }

        function isCopy(from, to) {
          return from.provider !== to.provider;
        }

        function getCopyFromParameters(from) {
          return {};
        }

        function getCopyToParameters(to) {
          return {};
        }
      }
    });
