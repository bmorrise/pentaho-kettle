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
 * The Folder Service
 *
 * The Folder Service, a collection of services used by the application
 *
 * @module services/folder.service
 * @property {String} name The name of the module.
 */
define(
    [
      "./services.service"
    ],
    function (servicesService) {
      "use strict";

      var factoryArray = [servicesService.name, factory];
      var module = {
        name: "folderService",
        factory: factoryArray
      };

      return module;

      /**
       * The fileService factory
       *
       * @return {Object} The fileService api
       */
      function factory(ss) {
        return {
          selectFolder: selectFolder,
          openFolder: openFolder,
          findFolderByPath: findFolderByPath,
          getPath: getPath
        };

        function getPath(folder) {
          if (!folder.root) {
            return folder.name;
          }
          var service = ss.get(folder.provider);
          if (!service) {
            return "";
          }
          return service.getPath(folder);
        }

        function openFolder(folder, callback) {
          var service = ss.get(folder.provider);
          if (service) {
            service.selectFolder(folder, function () {
              if (callback) {
                callback();
              }
            });
          }
        }

        /**
         * Sets variables showRecents, folder, and selectedFolder according to the contents of parameter
         *
         * @param {Object} folder - folder object
         * @param {Function} callback - an optional callback for when selection completes
         */
        function selectFolder(folder, filters, callback) {
          var service = ss.get(folder.provider);
          if (service) {
            service.selectFolder(folder, filters, function () {
              if (callback) {
                callback();
              }
            });
          } else {
            callback();
          }
        }

        function findFolderByPath(tree, folder, path) {
          var service = ss.get(folder.provider);
          var node = service.findRootNode(tree, folder, path);
          var parts = service.parsePath(path, folder);
          if (!parts) {
            return node;
          }
          return _findFolder(node, parts, 0);
        }

        function _findFolder(node, parts, index) {
          var children = node.children;
          for (var i = 0; i < children.length; i++) {
            if (children[i].name === parts[index]) {
              children[i].open = true;
              if (index < parts.length - 1) {
                return _findFolder(children[i], parts, ++index);
              } else {
                return children[i];
              }
            }
          }

          var folder = ss.get(node.provider).createFolder(node, parts[index]);
          children.push(folder);
          if (index < parts.length - 1) {
            return _findFolder(folder, parts, ++index);
          } else {
            return folder;
          }
        }
      }
    });
