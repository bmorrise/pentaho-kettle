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

      var factoryArray = [servicesService.name, "$q", factory];
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
      function factory(ss, $q) {
        return {
          selectFolder: selectFolder,
          openFolder: openFolder,
          findFolderByPath: findFolderByPath,
          getPath: getPath,
          deleteFolder: deleteFolder,
          addFolder: addFolder,
          createFolder: createFolder
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

        function openFolder(folder) {
          return selectFolder(folder);
        }

        /**
         * Sets variables showRecents, folder, and selectedFolder according to the contents of parameter
         *
         * @param {Object} folder - folder object
         * @param {String} filters - file filters
         * @param {Function} callback - an optional callback for when selection completes
         */
        function selectFolder(folder, filters) {
          return $q(function(resolve, reject) {
            var service = ss.get(folder.provider);
            if (service) {
              folder.loading = true;
              service.selectFolder(folder, filters).then(function() {
                folder.loading = false;
                resolve();
              }, function () {
                resolve();
              });
            } else {
              resolve();
            }
          });
        }

        function findFolderByPath(tree, folder, path) {
          var service = ss.get(folder.provider);
          var node = service.findRootNode(tree, folder, path);
          node.open = true;
          if (node) {
            var parts = service.parsePath(path, folder);
            if (!parts) {
              return node;
            }
            return _findFolder(node, parts, 0);
          }
          return null;
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

        function deleteFolder(tree, folder) {
          return $q(function(resolve, reject) {
            var parentFolder = findFolderByPath(tree, folder, _getParent(folder.path));
            ss.get(folder.provider).deleteFiles([folder]).then(function(response) {
              var index = parentFolder.children.indexOf(folder);
              parentFolder.children.splice(index, 1);
              resolve(parentFolder);
            }, function(response) {
              reject(response);
            });
          });
        }

        function _getParent(path) {
          return path.substr(0, path.lastIndexOf("/"));
        }

        function createFolder(folder) {
          return $q(function(resolve, reject) {
            ss.get(folder.provider).addFolder(folder).then(function(response) {
              folder.new = false;
              folder.date = response.data.date;
              resolve();
            }, function(response) {
              reject(response.status);
            });
          });
        }

        function addFolder(parentFolder) {
          var folder = {};
          var name = _getFolderName(parentFolder);
          folder.parent = parentFolder.path;
          folder.path = parentFolder.path + (parentFolder.path.charAt(parentFolder.path.length - 1) === "/" ? "" : "/") + name;
          folder.name = name;
          folder.new = true;
          folder.autoEdit = true;
          folder.type = "folder";
          folder.children = [];
          folder.provider = parentFolder.provider;
          folder.connection = parentFolder.connection; //TODO: Needs to be abstracted out
          folder.canEdit = true;
          folder.canAddChildren = true;
          folder.root = parentFolder.root;
          parentFolder.children.splice(0, 0, folder);
        }

        /**
         * Sets the default folder name to "New Folder" plus an incrementing integer
         * each time there is a folder called "New Folder <i+1>"
         *
         * @return {string} - Name of the folder
         * @private
         */
        function _getFolderName(parentFolder) {
          var name = "New Folder";
          var index = 0;
          var check = name;
          var search = true;
          while (search) {
            var found = false;
            for (var i = 0; i < parentFolder.children.length; i++) {
              if (parentFolder.children[i].name === check) {
                found = true;
                break;
              }
            }
            if (found) {
              index++;
              check = name + " " + index;
            } else {
              search = false;
            }
          }
          return check;
        }

        function updateDirectories(folder, oldPath, newPath) {
          folder.path = newPath;
          for (var i = 0; i < folder.children.length; i++) {
            _updateDirectories(folder.children[i], oldPath, newPath);
          }
          for (var k = 0; k < vm.recentFiles.length; k++) {
            if ((vm.recentFiles[k].path + "/").lastIndexOf(oldPath + "/", 0) === 0) {
              dt.updateRecentFiles(oldPath, newPath).then(function () {
                dt.getRecentFiles().then(_populateRecentFiles);
              });
              break;
            }
          }
        }

        /**
         * Update all child folder paths on parent rename
         *
         * @param {Object} child - Folder Object
         * @param {String} oldPath - String path of old directory path
         * @param {String} newPath - String path of new directory path
         * @private
         */
        function _updateDirectories(child, oldPath, newPath) {
          child.parent = child.parent.replace(oldPath, newPath);
          child.path = child.path.replace(oldPath, newPath);
          for (var i = 0; i < child.children.length; i++) {
            _updateDirectories(child.children[i], oldPath, newPath);
          }
        }
      }
    });
