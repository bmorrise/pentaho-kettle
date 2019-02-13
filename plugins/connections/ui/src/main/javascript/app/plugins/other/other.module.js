define([
  "angular",
  "./other.config",
  "./components/step1/step1.component"
], function(angular, config, step1Component) {
  "use strict";

  var module = {
    name: "other-vfs-plugin",
    scheme: "other",
    label: "Other"
  };

  activate();

  return module;

  /**
   * Creates angular module with dependencies.
   *
   * @private
   */
  function activate() {
    angular.module(module.name, [])
      .component(step1Component.name, step1Component.options)
      .config(config);
  }
});
