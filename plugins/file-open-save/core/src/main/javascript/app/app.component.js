/*!
 * HITACHI VANTARA PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2017-2018 Hitachi Vantara. All rights reserved.
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
 * The File Open and Save Main App component.
 *
 * This provides the main component for supporting the file open and save functionality.
 * @module app.component
 * @property {String} name The name of the Angular component.
 * @property {Object} options The JSON object containing the configurations for this component.
 **/
define([
  "angular",
  "./services/data.service",
  "./services/file.service",
  "./services/folder.service",
  "./services/modal.service",
  "text!./app.html",
  "pentaho/i18n-osgi!file-open-save.messages",
  "./components/utils",
  "css!./app.css"
], function (angular, dataService, fileService, folderService, modalService, template, i18n, utils) {
  "use strict";

  var options = {
    bindings: {},
    template: template,
    controllerAs: "vm",
    controller: appController
  };

  appController.$inject = [
    dataService.name,
    fileService.name,
    folderService.name,
    modalService.name,
    "$location", "$timeout", "$interval", "$state", "$q", "$document"];

  /**
   * The App Controller.
   *
   * This provides the controller for the app component.
   *
   * @param {Object} dt - Angular service that contains helper functions for the app component controller
   * @param {Function} $location - Angular service used for parsing the URL in browser address bar
   * @param {Object} $timeout - Angular wrapper around window.setTimeout
   * @param fileService
   * @param folderService
   * @param $state
   * @param $q
   */
  function appController(dt, fileService, folderService, modalService, $location, $timeout, $interval, $state, $q, $document) {
    var vm = this;
    vm.$onInit = onInit;
    vm.openFolder = openFolder;
    vm.selectFolder = selectFolder;
    vm.selectFolderByPath = selectFolderByPath;
    vm.onRenameFile = onRenameFile;
    vm.onCreateFolder = onCreateFolder;
    vm.onMoveFiles = onMoveFiles;
    vm.onCopyFiles = onCopyFiles;
    vm.onSelectFile = onSelectFile;
    vm.onDeleteFile = onDeleteFile;
    vm.openClicked = openClicked;
    vm.saveClicked = saveClicked;
    vm.okClicked = okClicked;
    vm.cancel = cancel;
    vm.onHighlight = onHighlight;
    vm.confirmError = confirmError;
    vm.cancelError = cancelError;
    vm.storeRecentSearch = storeRecentSearch;
    vm.renameError = renameError;
    vm.recentsHasScrollBar = recentsHasScrollBar;
    vm.addDisabled = addDisabled;
    vm.deleteDisabled = deleteDisabled;
    vm.upDisabled = upDisabled;
    vm.refreshDisabled = refreshDisabled;
    vm.onKeyUp = onKeyUp;
    vm.getPlaceholder = getPlaceholder;
    vm.getSelectedFolderName = getSelectedFolderName;
    vm.isSaveEnabled = isSaveEnabled;
    vm.isShowRecents = isShowRecents;
    vm.getFiles = getFiles;
    vm.getPath = getPath;

    vm.onAddFolder = onAddFolder;
    vm.onUpDirectory = onUpDirectory;
    vm.onRefreshFolder = onRefreshFolder;
    vm.onBackHistory = onBackHistory;

    vm.backHistoryDisabled = backHistoryDisabled;
    vm.currentRepo = "";
    vm.selectedFolder = "";
    vm.fileToSave = "";
    vm.showError = false;
    vm.errorType = 0;
    vm.loading = true;
    vm.fileLoading = false;
    vm.searching = false;
    vm.state = $state;
    vm.searchResults = [];
    vm.status = "";
    var history = [];

    /**
     * The $onInit hook of components lifecycle which is called on each controller
     * after all the controllers on an element have been constructed and had their
     * bindings initialized. We use this hook to put initialization code for our controller.
     */
    function onInit() {
      vm.searchPlaceholder = i18n.get("file-open-save-plugin.app.header.search.placeholder");
      vm.saveFileNameLabel = i18n.get("file-open-save-plugin.app.save.file-name.label");
      vm.openButton = i18n.get("file-open-save-plugin.app.open.button");
      vm.cancelButton = i18n.get("file-open-save-plugin.app.cancel.button");
      vm.saveButton = i18n.get("file-open-save-plugin.app.save.button");
      vm.okButton = i18n.get("file-open-save-plugin.app.ok.button");
      vm.confirmButton = i18n.get("file-open-save-plugin.app.save.button");
      vm.saveFileNameLabel = i18n.get("file-open-save-plugin.app.save.file-name.label");
      vm.addFolderText = i18n.get("file-open-save-plugin.app.add-folder.button");
      vm.removeText = i18n.get("file-open-save-plugin.app.delete.button");
      vm.loadingTitle = i18n.get("file-open-save-plugin.loading.title");
      vm.loadingMessage = i18n.get("file-open-save-plugin.loading.message");
      vm.showRecents = false;
      vm.files = [];
      vm.includeRoot = false;
      vm.autoExpand = false;
      vm.searchString = "";
      _resetFileAreaMessage();

      vm.filename = $location.search().filename;
      vm.fileType = $location.search().fileType;
      vm.origin = $location.search().origin;
      vm.filters = $location.search().filters;
      vm.tree = [
        {name: "Recents", hasChildren: false, provider: "recents", order: 0}
      ];
      vm.folder = vm.tree[0];
      vm.selectedFolder = "";
      $timeout(function () {
        var state = $state.current.name;
        vm.headerTitle = i18n.get("file-open-save-plugin.app.header." + state + ".title");
        if (!$state.is('selectFolder')) {
          dt.getDirectoryTree($location.search().filter).then(_populateTree);
          dt.getRecentFiles().then(_populateRecentFiles);
          vm.showRecents = true;
        } else {
          dt.getDirectoryTree("false").then(_populateTree);
        }
        dt.getRecentSearches().then(_populateRecentSearches);
        vm.loading = false;
      });

      angular.element($document).bind('keydown', function (event) {
        var ctrlKey = event.metaKey || event.ctrlKey;
        if (event.keyCode === 38 && ctrlKey) {
          onUpDirectory();
        }
      });
    }

    /**
     * Sets the folder directory tree
     *
     * @param {Object} response - $http response from call to the data service
     * @private
     */
    function _populateTree(response) {
      vm.tree = vm.tree.concat(response.data);
      var path = $location.search().path;
      var connection = $location.search().connection;
      var provider = $location.search().provider;
      if (path) {
        vm.folder = {path: path, connection: connection, provider: provider};
        vm.autoExpand = true;
        selectFolderByPath(path);
      }
      _setFileToSaveName();
    }

    /**
     * Sets the recents folders
     *
     * @param {Object} response - $http response from call to the data service
     * @private
     */
    function _populateRecentFiles(response) {
      vm.recentFiles = response.data;
    }

    /**
     * Determines if the Recents view has a vertical scrollbar
     * @return {boolean} - true if Recents view has a vertical scrollbar, false otherwise
     */
    function recentsHasScrollBar() {
      var recentsView = document.getElementsByClassName("recentsView");
      return recentsView.scrollHeight > recentsView.clientHeight;
    }

    /**
     * Gets the active file name from Spoon to set vm.fileToSave
     * @private
     */
    function _setFileToSaveName() {
      if ($state.is("save")) {
        if (vm.filename !== undefined) {
          vm.fileToSave = vm.filename;
        } else {
          dt.getActiveFileName().then(function (response) {
            vm.fileToSave = response.data.fileName;
          }, function () {
            vm.fileToSave = "";
          });
        }
      }
    }

    /**
     * Open the folder
     * @param {folder} folder - Folder to open
     */
    function openFolder(folder) {
      return folderService.openFolder(folder);
    }

    /**
     * Sets variables showRecents, folder, and selectedFolder according to the contents of parameter
     *
     * @param {Object} folder - folder object
     */
    function selectFolder(folder) {
      if (history.length === 0 || history[history.length - 1].path !== folder.path) {
        addHistory(folder);
      }
      vm.searchString = "";
      if (vm.searching) {
        _clearSearch();
      }
      _resetFileAreaMessage();
      vm.files = [];
      if (folder !== vm.folder || vm.folder.loaded === false) {
        vm.folder = folder;
        vm.showRecents = false;
        if (folder.provider === "recents") {
          vm.showRecents = true;
        } else {
          vm.fileLoading = true;
          folderService.selectFolder(folder, vm.filters).then(function () {
            vm.fileLoading = false;
          });
        }
      }
    }

    /**
     * Selects a folder according to the path parameter
     *
     * @param {String} path - path to file
     */
    function selectFolderByPath(path) {
      var folder = folderService.findFolderByPath(vm.tree, vm.folder, path);
      if (folder) {
        $timeout(function () {
          selectFolder(folder);
        });
      }
    }

    /**
     * Calls function to open folder if type of file is a folder. Else it opens either
     * the recent file or other file and closes the browser.
     *
     * @param {Object} file - file object
     */
    function onSelectFile(file) {
      if (file.type === "folder") {
        vm.searchString = "";
        selectFolder(file);
      } else if ($state.is("open")) {
        _open(file);
      }
    }

    /**
     * Get file list for current state
     * @returns {Array} - Search result files or selected folder children
     */
    function getFiles() {
      if (vm.searchString !== "") {
        return vm.searchResults;
      }
      return vm.folder.children;
    }

    /**
     * Sets the message for the file area to No Results
     * @private
     */
    function _setFileAreaMessage() {
      if (vm.showMessage) {
        vm.fileAreaMessage = i18n.get("file-open-save-plugin.app.middle.no-results.message");
      }
    }

    /**
     * Resets the showMessage and file area message to default values
     * @private
     */
    function _resetFileAreaMessage() {
      vm.showMessage = false;
      vm.fileAreaMessage = i18n.get("file-open-save-plugin.app.middle.no-recents.message");
    }

    /**
     * Sets the selected file to the file parameter to highlight it in UI.
     * Also, sets the file to save text according to the selected file.
     *
     * @param {Object} files - file objects
     */
    function onHighlight(files) {
      vm.files = files;
      if (files.length === 1) {
        vm.fileToSave = files[0].type === "folder" ? vm.fileToSave : files[0].name;
      }
    }

    /**
     * Called when user clicks "Open"
     */
    function openClicked() {
      if (vm.file && vm.file.type === "folder") {
        vm.searchString = "";
        selectFolder(vm.file);
      } else {
        _open(vm.file);
      }
    }

    /**
     * Called when user clicks "Save"
     */
    function saveClicked() {
      _save(false);
    }

    //TODO: Fix this crap
    /**
     * Calls data service to open file
     * @param {Object} file - File object
     * @private
     */
    function _open(file) {
      try {
        if (vm.selectedFolder === "Recents" && vm.origin === "spoon") {
          dt.openRecent(file.repository + ":" + (file.username ? file.username : ""),
              file.objectId).then(function (response) {
            _closeBrowser();
          }, function (response) {
            _triggerError(16);
          });
        } else {
          select(file.objectId, file.name, file.path, file.type);
        }
      } catch (e) {
        if (file.repository) {
          dt.openRecent(file.repository + ":" + (file.username ? file.username : ""),
              file.objectId).then(function (response) {
            _closeBrowser();
          }, function (response) {
            _triggerError(16);
          });
        } else {
          dt.openFile(file.objectId, file.type, file.path).then(function (response) {
            _closeBrowser();
          });
        }
      }
    }

    //TODO: Fix this crap
    /**
     * Calls data service to save file if there is no error
     * @param {boolean} override - Override file?
     * @private
     */
    function _save(override) {
      if (_isInvalidName()) {
        _triggerError(17);
      }
      else if (override || !_isDuplicate()) {
        try {
          dt.checkForSecurityOrDupeIssues(vm.folder.path, vm.fileToSave, vm.file === null ? null : vm.file.name,
              override).then(function (response) {
            if (response.status === 200) {
              select("", vm.fileToSave, vm.folder.path, "");
            } else {
              _triggerError(3);
            }
          });
        } catch (e) {
          dt.saveFile(vm.folder.path, vm.fileToSave, vm.file === null ? null : vm.file.name, override)
              .then(function (response) {
                if (response.status === 200) {
                  _closeBrowser();
                } else {
                  _triggerError(3);
                }
              });
        }
      } else {
        _triggerError(1);
      }
    }

    /**
     * Handler for when the ok button is clicked
     */
    function okClicked() {
      select(vm.file.objectId, vm.file.name, vm.file.path, vm.file.type, vm.file.connection);
    }

    /**
     * Called if user clicks cancel in either open or save to close the browser
     */
    function cancel() {
      _closeBrowser();
    }

    /**
     * Called to close the browser if there is no current error
     * @private
     */
    function _closeBrowser() {
      if (!vm.showError) {
        close();
      }
    }

    /**
     * Sets the error type and boolean to show the error dialog.
     * @param {Number} type - the type of error (0-6)
     * @private
     */
    function _triggerError(type) {
      vm.errorType = type;
      vm.showError = true;
    }

    /**
     * Called if user clicks the confirmation button on an error dialog to handle the
     * error and cancel it
     */
    function confirmError() {
      switch (vm.errorType) {
        case 1: // File exists...override
          _save(true);
          break;
        case 5: // Delete File
          commitRemove();
          break;
        case 6: // Delete Folder
          commitRemove();
          break;
        case 21:
          commitRemove();
          break;
        default:
          break;
      }
      cancelError();
    }

    /**
     * Resets the error variables as if no error is present
     */
    function cancelError() {
      vm.errorType = 0;
      vm.showError = false;
    }

    /**
     * Shows an error if one occurs during rename
     * @param {number} errorType - the number corresponding to the appropriate error
     * @param {object} file - file object
     */
    function renameError(errorType, file) {
      if (file) {
        var index = vm.folder.children.indexOf(file);
        vm.folder.children.splice(index, 1);
      }
      $timeout(function () {
        _triggerError(errorType);
      });
    }

    /**
     * Stores the most recent search
     */
    function storeRecentSearch() {
      if (vm.searchString !== "") {
        if (vm.recentSearches.indexOf(vm.searchString) === -1) {
          vm.recentSearches.push(vm.searchString);
          dt.storeRecentSearch(vm.searchString).then(_populateRecentSearches);
        }
      }
    }

    /**
     * Determines if add button is to be disabled
     * @return {boolean} - True if no folder is selected or if Recents is selected, false otherwise
     */
    function addDisabled() {
      return vm.folder && !vm.folder.canAddChildren;
    }


    /**
     * Determines if the refresh button is to be disabled
     * @returns {boolean} - True if current folder is selected and not loaded
     */
    function refreshDisabled() {
      return vm.folder && !vm.folder.loaded;
    }

    /**
     * Determines if the up directory button is to be disabled
     * @returns {boolean} - True if current folder is select and not loaded
     */
    function upDisabled() {
      return vm.folder && !vm.folder.loaded;
    }

    /**
     * Determines if delete button is to be disabled
     * @return {boolean} - True if no folder is selected, if Recents is selected,
     * or if root folder/file is selected, false otherwise
     */
    function deleteDisabled() {
      if (vm.files.length > 0) {
        for (var i = 0; i < vm.files.length; i++) {
          if (!vm.files[i].canEdit) {
            return true;
          }
        }
        return false;
      } else if (vm.folder) {
        return !vm.folder.canEdit;
      }
      return false;
    }

    /**
     * @param {Object} response - Response object
     * Populates the most recent searches
     * @private
     */
    function _populateRecentSearches(response) {
      vm.recentSearches = response.data;
    }

    /**
     * Called if user selects to delete the selected folder or file.
     */
    //TODO: Change message based on whether or not multiple files are selected
    function onDeleteFile(files) {
      if (files) {
        vm.files = files;
      }
      if (vm.files.length === 1) {
        if (vm.files[0].type === "folder") {
          _triggerError(6);
        } else {
          _triggerError(5);
        }
      } else {
        if (vm.files.length === 0) {
          _triggerError(6);
        } else {
          _triggerError(21);
        }
      }
    }

    /**
     * Calls the service for removing the file
     */
    // TODO: Fix issue where if you double-click into a folder and you delete it's a select file
    function commitRemove() {
      if (vm.files.length === 0) {
        folderService.deleteFolder(vm.tree, vm.folder).then(function (parentFolder) {
          vm.folder = null;
          selectFolder(parentFolder);
          vm.file = null;
          vm.searchString = "";
          vm.showMessage = false;
          dt.getRecentFiles().then(_populateRecentFiles);
        }, function (response) {
          if (response.status === 406) {// folder has open file
            _triggerError(13);
          } else {
            _triggerError(8);
          }
        });
      } else {
        fileService.deleteFiles(vm.folder, vm.files).then(function (response) {
          vm.files = [];
          dt.getRecentFiles().then(_populateRecentFiles);
        }, function (response) {
          if (vm.file.type === "folder") {
            if (response.status === 406) {// folder has open file
              _triggerError(13);
            } else {
              _triggerError(8);
            }
          } else if (response.status === 406) {// file is open
            _triggerError(14);
          } else {
            _triggerError(9);
          }
        });
      }
    }

    /**
     * Called if user selects to add a folder or file while Recents is not selected.
     */
    function onAddFolder() {
      folderService.addFolder(vm.folder);
    }

    /**
     * Create new folder
     * @param {folder} folder - folder object definition
     * @returns {*} - A promise for the creation
     */
    function onCreateFolder(folder) {
      return $q(function (resolve, reject) {
        folderService.createFolder(folder).then(function () {
          resolve();
        }, function (status) {
          switch (status) {
            case 304:
              _triggerError(4);
              break;
          }
          reject();
        });
      });
    }

    /**
     * Copy files to a new directory
     * @param {array} from - List of file objects to move
     * @param {file} to - File object to move
     * @returns {*} - a promise to handle the creation
     */
    function onCopyFiles(from, to) {
      copyFiles(from, to);
    }

    /**
     * Move files to a new directory
     * @param {array} from - List of file objects to move
     * @param {file} to - File object to move
     * @returns {*} - a promise to handle the creation
     */
    function onMoveFiles(from, to) {
      if (fileService.isCopy(from[0], to)) {
        copyFiles(from, to);
      } else {
        moveFiles(from, to);
      }
    }

    /**
     * Move files from one directory to another
     * @param from
     * @param to
     */
    function moveFiles(from, to) {
      fileService.moveFiles(from, to).then(function (response) {
        var id = response.data;
        fileService.handleStatus(id).then(function(data) {
          completeMove(from, to, data.succeeded);
        });
      }, function (status) {
        switch (status) {
          case 304:
            _triggerError(20);
            break;
        }
      });
    }

    /**
     *
     * @param from
     * @param to
     * @param movedFiles
     */
    function completeMove(from, to, movedFiles) {
      if (movedFiles.length !== from.length) {
        //TODO: Show an error that speaks to some files couldn't be moved
        _triggerError(20);
      }
      to.loaded = false;
      var parentPath = from[0].path.substr(0, from[0].path.lastIndexOf("/"));
      var parentFolder = folderService.findFolderByPath(vm.tree, from[0], parentPath);
      for (var i = from.length - 1; i >= 0; i--) {
        if (movedFiles.indexOf(from[i].path) !== -1) {
          parentFolder.children.splice(parentFolder.children.indexOf(from[i]), 1);
          vm.files.splice(vm.files.indexOf(from[i]), 1);
        }
      }
      onRefreshFolder();
    }

    /**
     * Copies a file from on provider to another
     * @param from
     * @param to
     */
    //TODO: Show which files were not copied and allow user to select up overwrite or rename
    function copyFiles(from, to) {
      fileService.copyFiles(from, to).then(function (response) {
        // TODO: If loaded, add the files, if not don't worry about it
        to.loaded = false;
        console.log("Copy Complete.");
        console.log(response.data);
        onRefreshFolder();
        // TODO: Show message if some couldn't be copied
      }, function (response) {
        console.log(response);
        console.log("Copy Failed.");
        onRefreshFolder();
        // TODO: Trigger error that files couldn't be copied
      });
    }

    /**
     * Renames a file
     * @param file
     * @param newPath
     * @returns {*}
     */
    function onRenameFile(file, newPath) {
      return $q(function (resolve, reject) {
        fileService.renameFile(file, newPath).then(function () {
          resolve();
        }, function (status) {
          switch (status) {
            case 409:
              _triggerError(11);
              break;
          }
          reject();
        });
      });
    }

    /**
     * Checks to see if the user has entered a file to save the same as a file already in current directory
     * NOTE: does not check for hidden files. That is done in the checkForSecurityOrDupeIssues rest call
     * @return {boolean} - true if duplicate, false otherwise
     * @private
     */
    function _isDuplicate() {
      if (vm.folder && vm.folder.children) {
        for (var i = 0; i < vm.folder.children.length; i++) {
          if (vm.fileToSave === vm.folder.children[i].name && vm.fileType === vm.folder.children[i].type) {
            vm.file = vm.folder.children[i];
            return true;
          }
        }
      }
      return false;
    }

    /**
     * Checks if the file name to save is valid or not. An invalid name contains forward or backward slashes
     * @returns {boolean} - true if the name is invalid, false otherwise
     * @private
     */
    function _isInvalidName() {
      return vm.fileToSave.match(/[\\\/]/g) !== null;
    }

    /**
     * Gets the key up event from the app
     *
     * @param {Object} event - Event Object
     */
    function onKeyUp(event) {
      if (event.keyCode === 13 && event.target.id !== "searchBoxId") {
        if ($state.is("open")) {
          if (vm.file !== null) {
            onSelectFile(vm.file);
          }
        } else if (!vm.showRecents) {
          _save(false);
        }
      }
    }

    /**
     * Determines if the browser is Internet Explorer.
     * If it is, it truncates the placeholder for the search box if it's width is greater than the
     * search box. It then adds ellipsis to the end of that string and returns that value.
     * If it is not Internet Explorer, it just returns the search box placeholder and any
     * truncation/ellipsis is handled using CSS. NOTE: this is a workaround for an IE bug
     * that doesn't allow placeholders to be ellipsis unless the input is readonly.
     * @return {string} - the Placeholder for the search box
     */
    function getPlaceholder() {
      var isIE = navigator.userAgent.indexOf("Trident") !== -1 && Boolean(document.documentMode);
      var retVal = vm.searchPlaceholder;
      if (vm.folder.path !== "Recents") {
        retVal += " " + vm.selectedFolder;
      } else {
        retVal += " " + vm.currentRepo;
      }
      if (isIE && utils.getTextWidth(retVal) > 210) {
        var tmp = "";
        for (var i = 0; i < retVal.length; i++) {
          tmp = retVal.slice(0, i);
          if (utils.getTextWidth(tmp) > 196) {
            break;
          }
        }
        retVal = tmp + "...";
      }
      return retVal;
    }

    /**
     * Returns input with first letter capitalized
     * @param {string} input - input string
     * @return {string} - returns input with first letter capitalized
     * @private
     */
    function _capsFirstLetter(input) {
      return input.charAt(0).toUpperCase() + input.slice(1);
    }

    /**
     * Returns the name of the selected folder
     *
     * @return {String} - "Search results in "<The name of the selected folder>", truncating with ellipsis accordingly
     */
    function getSelectedFolderName() {
      var retVal = i18n.get("file-open-save-plugin.app.search-results-in.label");
      if (vm.selectedFolder === "") {
        // if (vm.selectedFolder === "" && isPentahoRepo()) {
        retVal += "\"" + vm.currentRepo;
      } else {
        retVal += "\"" + vm.selectedFolder;
      }
      if ($state.is("open") && utils.getTextWidth(retVal) > 435) {
        retVal = utils.truncateString(retVal, 426) + "...";
      } else if ($state.is("save") && utils.getTextWidth(retVal) > 395) {
        retVal = utils.truncateString(retVal, 386) + "...";
      }
      return retVal + "\"";
    }

    /**
     * Returns whether or not the save button should be enabled
     *
     * @returns {boolean} - true if the save button should be enabled
     */
    function isSaveEnabled() {
      return vm.fileToSave === '' || vm.folder.path === 'Recents';
    }

    /**
     * Returns whether or not the recents panel should be shown
     *
     * @returns {boolean} - true if recents should be shown
     */
    function isShowRecents() {
      if (vm.recentFiles) {
        return !vm.showMessage && vm.showRecents && vm.recentFiles.length > 0 && !$state.is('selectFolder') && !$state.is('selectFile');
      }
    }

    /**
     * Get the path of the current selected file
     * @returns {string} - the path of the currently selected file/folder
     */
    function getPath() {
      if (vm.files.length === 1) {
        return folderService.getPath(vm.files[0])
      } else {
        return folderService.getPath(vm.folder)
      }
    }

    /**
     * Navigate up a directory
     */
    function onUpDirectory() {
      if (vm.folder) {
        var path = folderService.getPath(vm.folder);
        selectFolderByPath(path.substr(0, path.lastIndexOf("/")));
      }
    }

    /**
     * Add a location to the history
     * @param {folder} folder - the folder to add to the history
     */
    function addHistory(folder) {
      history.push(folder);
    }

    /**
     * Navigate back in the history
     */
    function onBackHistory() {
      vm.file = null;
      history.pop();
      selectFolder(history[history.length - 1]);
    }

    /**
     * Refresh the currently visible folder
     */
    function onRefreshFolder() {
      if (vm.folder) {
        vm.folder.loaded = false;
        selectFolder(vm.folder);
      }
    }

    /**
     * Whether or not to disable the back history button
     * @returns {boolean} - Whether or not to disable the back history button
     */
    function backHistoryDisabled() {
      return history.length <= 1;
    }
  }

  return {
    name: "fileOpenSaveApp",
    options: options
  };
})
;
