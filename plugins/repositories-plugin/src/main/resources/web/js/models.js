define( [
  'repositories'
  ],
  function ( repoConnectionApp ) {

    repoConnectionApp.service("repositoryTypesModel", function() {
      this.repositoryTypes = JSON.parse(getRepositoryTypes());
      this.selectedRepository = null;
    });

    repoConnectionApp.service("repositoriesModel", function() {
      this.repositories = JSON.parse(getRepositories());
      this.selectedRepository = null;
    });

    repoConnectionApp.service("pentahoRepositoryModel",function() {
      this.displayName = "";
      this.url = getDefaultUrl();
      this.description = "Pentaho repository | " + getDefaultUrl();
      this.isDefault = false;
    });

    repoConnectionApp.service("kettleFileRepositoryModel", function() {
      this.displayName = "";
      this.location = "";
      this.doNotModify = false;
      this.showHiddenFolders = false;
      this.description = "Kettle file repository";
      this.isDefault = false;
    });

    repoConnectionApp.service("kettleDatabaseRepositoryModel", function() {
      this.databases = JSON.parse(getDatabases());
      this.displayName = "";
      this.databaseConnection = "None";
      this.description = "Kettle database repository";
      this.isDefault = false;
      this.selectedDatabase = null;
    });

    repoConnectionApp.service("repositoryConnectModel", function() {
      this.username = "";
      this.password = "";
    });

});
