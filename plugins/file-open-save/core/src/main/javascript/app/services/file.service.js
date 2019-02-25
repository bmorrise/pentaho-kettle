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
 * The File Service
 *
 * The File Service, a collection of endpoints used by the application
 *
 * @module services/file.service
 * @property {String} name The name of the module.
 */
define(
    [
      "pentaho/i18n-osgi!file-open-save.messages",
      "./services.service",
      "./helper.service",
      "./modal.service",
      "../components/utils"
    ],
    function (i18n, servicesService, helperService, modalService, utils) {
      "use strict";

      var factoryArray = [servicesService.name, helperService.name, modalService.name, "$q", "$interval", factory];
      var module = {
        name: "fileService",
        factory: factoryArray
      };

      return module;

      /**
       * The fileService factory
       *
       * @return {Object} The fileService api
       */
      function factory(ss, helperService, modalService, $q, $interval) {
        var baseUrl = "/cxf/browser";
        return {
          deleteFiles: deleteFiles,
          renameFile: renameFile,
          moveFiles: moveFiles,
          copyFiles: copyFiles,
          isCopy: isCopy,
          getStatus: getStatus,
          handleStatus: handleStatus,
          proceed: proceed
        };

        function deleteFiles(folder, files) {
          return $q(function (resolve, reject) {
            ss.get(folder.provider).deleteFiles(files).then(function (response) {
              for (var i = 0; i < files.length; i++) {
                var index = folder.children.indexOf(files[i]);
                folder.children.splice(index, 1);
              }
              resolve(response);
            }, function (response) {
              reject(response);
            });
          });
        }

        function renameFile(file, newPath) {
          return $q(function (resolve, reject) {
            ss.get(file.provider).renameFile(file, newPath).then(function (response) {
              resolve();
            }, function (response) {
              reject(response.status);
            });
          });
        }

        function moveFiles(from, to) {
          return $q(function (resolve, reject) {
            ss.get(to.provider).moveFiles(from, to).then(function (response) {
              resolve(response.data);
            }, function (response) {
              reject(response.status);
            });
          });
        }

        // TODO: Refactor other methods so they just get the parameters
        function copyFiles(from, to) {
          var paths = [];
          for (var i = 0; i < from.length; i++) {
            paths.push(from[i].path);
          }
          var fromParameters = ss.get(from[0].provider).getCopyFromParameters(from[0]);
          var toParameters = ss.get(to.provider).getCopyToParameters(to);
          var parameters = utils.concatParameters(fromParameters, toParameters);
          parameters["newPath"] = to.path;
          parameters["fromProvider"] = from[0].provider;
          parameters["toProvider"] = to.provider;
          return helperService.httpPost([baseUrl, "copyFiles"].join("/") + utils.buildParameters(parameters), paths);
        }

        function isCopy(from, to) {
          return ss.get(from.provider).isCopy(from, to) && ss.get(to.provider).isCopy(from, to);
        }

        function getStatus(id) {
          return helperService.httpGet([baseUrl, "status", id].join("/"));
        }

        function proceed(id, overwrite) {
          return helperService.httpGet([baseUrl, "proceed", id].join("/") + "?overwrite=" + overwrite);
        }

        function handleStatus(id) {
          return $q(function (resolve, reject) {
            var showingModal = false;
            // TODO: Make this used with copy as well
            var status = $interval(function () {
              getStatus(id).then(function (response) {
                var result = response.data;
                if (result.status === "FILE_COLLISION" && showingModal === false) {
                  showingModal = true;
                  var name = utils.getFilename(result.data.from);
                  modalService.open("overwrite-warning",
                      i18n.get("file-open-save-plugin.error.file-exists.title"),
                      i18n.get("file-open-save-plugin.error.file-exists.body", {name: name})).then(function (result) {
                    proceed(id, result).then(function (response) {
                      showingModal = false;
                    });
                  });
                }
                if (result.status === "PENDING") {
                  // TODO: This is where the pending status will show up on the dialog
                }
                if (result.status === "SUCCESS" || result.status === "ERROR") {
                  $interval.cancel(status);
                  resolve(result.data);
                }
              });
            }, 100);
          });
        }

        //TODO: Add a rename function to folders so I can traverse and rename

        /**
         * Calls vm.onError using the parameter errorType
         * @param {number} errorType - the number corresponding to the appropriate error
         * @private
         */
        function _doError(errorType, file) {
          vm.onError({errorType: errorType, file: file});
        }

        /**
         * Checks for a duplicate name
         *
         * @param {String} name - file name to check if it already exists within vm.files
         * @param {Object} file - File Object
         * @return {Boolean} true if it vm.files already has a file named "name", false otherwise
         * @private
         */
        function _hasDuplicate(name, file) {
          for (var i = 0; i < vm.files.length; i++) {
            var check = vm.files[i];
            if (check !== file) {
              if (check.name.toLowerCase() === name.toLowerCase() && check.type === file.type) {
                return true;
              }
            }
          }
          return false;
        }

        /**
         * Checks if the file name is valid or not. An invalid name contains forward or backward slashes
         * @returns {boolean} - true if the name is invalid, false otherwise
         * @private
         */
        function _hasInvalidChars(name) {
          return name.match(/[\\\/]/g) !== null;
        }

        // TODO: Make sure all this happens now
        // var newName = current;
        // if (_hasInvalidChars(current)) {
        //   errorCallback();
        //   _doError(18);
        //   newName = previous;
        // }
        // else if (_hasDuplicate(current, file)) {
        //   file.newName = current;
        //   errorCallback();
        //   _doError(file.type === "folder" ? 2 : 7);
        //   newName = previous;
        // }
        // file.new = false;
        // dt.create(file.parent, newName).then(function(response) {
        //   var index = file.path.lastIndexOf("/");
        //   var oldPath = file.path;
        //   var newPath = file.path.substr(0, index) + "/" + newName;
        //   var id = response.data.objectId;
        //   vm.onRename({file: file, oldPath: oldPath, newPath: newPath});
        //   file.objectId = id;
        //   file.parent = response.data.parent;
        //   file.path = response.data.path;
        //   file.name = newName;
        // }, function() {
        //   _doError(4, file);
        // });


        // if (_hasInvalidChars(current)) {
        //   errorCallback();
        //   _doError(19);
        //   return;
        // }
        // dt.rename(file.objectId.id, file.path, current, file.type, file.name).then(function (response) {
        //   file.name = current;
        //   file.objectId = response.data;
        //   if (file.type === "folder") {
        //     var index = file.path.lastIndexOf("/");
        //     var oldPath = file.path;
        //     var newPath = file.path.substr(0, index) + "/" + current;
        //     vm.onRename({file: file, oldPath: oldPath, newPath: newPath});
        //   }
        // }, function (response) {
        //   file.newName = current;
        //   errorCallback();
        //   if (response.status === 304 || response.status === 500) {
        //     _doError(file.type === "folder" ? 10 : 11);
        //   } else if (response.status === 409) {
        //     if (_hasDuplicate(current, file)) {
        //       _doError(file.type === "folder" ? 2 : 7);
        //     } else {
        //       _doError(file.type === "folder" ? 10 : 11);
        //     }
        //   } else if (response.status === 406) {
        //     _doError(file.type === "folder" ? 15 : 12);
        //   } else {
        //     _doError(file.type === "folder" ? 10 : 11);
        //   }
        // });
      }
    });
