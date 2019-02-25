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
        name: "repositoryService",
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
          provider: "repository",
          selectFolder: selectFolder,
          getPath: getPath,
          createFolder: createFolder,
          findRootNode: findRootNode,
          parsePath: parsePath,
          addFolder: addFolder,
          deleteFile: deleteFile,
          renameFile: renameFile,
          moveFile: moveFile
        };

        function findRootNode(tree, folder, subtree) {
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
              getFiles(folder.path).then(function(response) {
                folder.loaded = true;
                folder.children = response.data.children;
                resolve();
              });
            } else {
              resolve();
            }
          });
        }

        function getPath(folder) {
          return folder.root + folder.path;
        }

        function createFolder(node, name) {
          return {name: name, path: node.path + "/" + name, canEdit: true, canAddChildren: true, children: [], open: true, type: "folder", provider: node.provider, root: node.root};
        }

        /**
         * Gets the directory tree for the currently connected repository
         *
         * @return {Promise} - a promise resolved once data is returned
         */
        function getFiles(path) {
          return helperService.httpGet([baseUrl, "loadFilesAndFolders", encodeURIComponent(path)].join("/"));
        }

        function addFolder(folder) {
          return helperService.httpPost([baseUrl, "create", encodeURIComponent(folder.parent), encodeURIComponent(folder.name)].join("/"), null).then(function(response) {
            folder.objectId = response.data.objectId;
          });
        }

        function deleteFile(file) {
          return helperService.httpDelete([baseUrl, "remove", encodeURIComponent(file.objectId), encodeURIComponent(file.name), encodeURIComponent(file.path), file.type].join("/"));
        }

        function renameFile(file, newPath) {
          var oldName = _getName(file.path);
          var newName = _getName(newPath);
          return helperService.httpPost([baseUrl, "rename", encodeURIComponent(file.objectId), encodeURIComponent(file.parent), encodeURIComponent(newName), file.type, oldName].join("/"));
        }

        function moveFile(file, newPath) {
          var oldName = _getName(file.path);
          var newName = _getName(newPath);
          newPath = newPath.substr(0, newPath.lastIndexOf("/"));
          return helperService.httpPost([baseUrl, "rename", encodeURIComponent(file.objectId), encodeURIComponent(newPath), encodeURIComponent(newName), file.type, oldName].join("/"));
        }

        function _getName(path) {
          var newPath = path.substr(path.lastIndexOf("/") + 1, path.length);
          return newPath.substr(0, newPath.lastIndexOf("."));
        }

        //TODO: Deal with this crap

      // else {
      //     //TODO: Move to service
      //     if ($state.is('selectFolder')) {
      //       if (isPentahoRepo()) {
      //         selectFolderByPath("/home");
      //       } else {
      //         selectFolderByPath("/");
      //       }
      //     }
      //   }
        //TODO: Move to repository service
        // dt.getCurrentRepo().then(function(response) {
        //   vm.currentRepo = response.data.name;
        //   vm.loading = false;
        // });

        //TODO: Move to repository service
        /**
         * Determines whether or no this is a pentaho repository
         *
         * @returns {boolean}
         * @private
         */
        function isPentahoRepo() {
          return vm.tree.children[0].children.length === 2 && vm.tree.children[0].children[0].name === "home";
        }


      }
    });
