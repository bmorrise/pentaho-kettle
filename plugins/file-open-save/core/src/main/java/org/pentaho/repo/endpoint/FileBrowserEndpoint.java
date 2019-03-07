/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.repo.endpoint;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.core.exception.KettleObjectExistsException;
import org.pentaho.di.core.exception.KettleTransException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.repo.controller.FileBrowserController;
import org.pentaho.repo.controller.RepositoryBrowserController;
import org.pentaho.repo.api.providers.Properties;
import org.pentaho.repo.api.providers.Tree;
import org.pentaho.repo.providers.repository.model.RepositoryDirectory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * Created by bmorrise on 5/12/17.
 */
public class FileBrowserEndpoint {

  private final RepositoryBrowserController repositoryBrowserController;
  private final FileBrowserController fileBrowserController;

  // TODO: Move properties into the post p
  public FileBrowserEndpoint( RepositoryBrowserController repositoryBrowserController,
                              FileBrowserController fileBrowserController ) {
    this.repositoryBrowserController = repositoryBrowserController;
    this.fileBrowserController = fileBrowserController;
  }

  @GET
  @Path( "/loadDirectoryTree{filter : (/filter)?}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadDirectoryTree( @PathParam( "filter" ) String filter ) {
    List<Tree> trees = fileBrowserController.load();
    return Response.ok( trees ).build();
  }

  @GET
  @Path( "/getFiles" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadDirectoryTree( @QueryParam( "provider" ) String type,
                                     @QueryParam( "connection" ) String connection,
                                     @QueryParam( "path" ) String path, @QueryParam( "filters" ) String filters ) {
    return Response
      .ok( fileBrowserController.getFiles( type, path, filters, Properties.create( "connection", connection ) ) )
      .build();
  }

  // TODO: Set errors to go with results
  @POST
  @Path( "/delete" )
  @Produces( { MediaType.APPLICATION_JSON } )
  @Consumes( { MediaType.APPLICATION_JSON } )
  public Response deleteFiles( @QueryParam( "provider" ) String type, @QueryParam( "connection" ) String connection,
                               List<String> paths ) {
    return Response
      .ok( fileBrowserController.deleteFiles( type, paths, Properties.create( "connection", connection ) ) )
      .build();
  }

  @PUT
  @Path( "/addFolder" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response addFolder( @QueryParam( "provider" ) String type, @QueryParam( "connection" ) String connection,
                             @QueryParam( "path" ) String path ) {
    return Response.ok( fileBrowserController.addFolder( type, path, Properties.create( "connection", connection ) ) )
      .build();
  }

  //TODO: Update type to provider

  @POST
  @Path( "/renameFile" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response renameFile( @QueryParam( "fromProvider" ) String fromProvider,
                              @QueryParam( "fromConnection" ) String fromConnection,
                              @QueryParam( "toProvider" ) String toProvider,
                              @QueryParam( "toConnection" ) String toConnection,
                              @QueryParam( "newPath" ) String newPath,
                              @QueryParam( "overwrite" ) Boolean overwrite,
                              @QueryParam( "path" ) String path ) {
    overwrite = overwrite != null ? overwrite : false;
    return Response.ok( fileBrowserController.renameFile( fromProvider, toProvider, path, newPath, overwrite,
      Properties.create( "fromConnection", fromConnection, "toConnection", toConnection ) ) ).build();
  }

  @POST
  @Path( "/copyFile" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response copyFile( @QueryParam( "fromProvider" ) String fromProvider,
                              @QueryParam( "fromConnection" ) String fromConnection,
                              @QueryParam( "toProvider" ) String toProvider,
                              @QueryParam( "toConnection" ) String toConnection,
                              @QueryParam( "newPath" ) String newPath,
                              @QueryParam( "overwrite" ) Boolean overwrite,
                              @QueryParam( "path" ) String path ) {

    overwrite = overwrite != null ? overwrite : false;
    return Response.ok( fileBrowserController.copyFile( fromProvider, toProvider, path, newPath, overwrite,
      Properties.create( "fromConnection", fromConnection, "toConnection", toConnection ) ) ).build();
  }

  @GET
  @Path( "/fileExists" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response fileExists( @QueryParam( "provider" ) String provider,
                              @QueryParam( "connection" ) String connection,
                              @QueryParam( "path" ) String path ) {

    return Response
      .ok( fileBrowserController.fileExists( provider, path, Properties.create( "connection", connection ) ) ).build();
  }

  @GET
  @Path( "/getNewName" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response getNewName( @QueryParam( "provider" ) String provider,
                              @QueryParam( "connection" ) String connection,
                              @QueryParam( "path" ) String path ) {

    return Response
      .ok( fileBrowserController.getNewName( provider, path, Properties.create( "connection", connection ) ) ).build();
  }

  /**
   * OLD ENDPOINTS
   **/
  @GET
  @Path( "/loadFile/{id}/{type}" )
  public Response loadFile( @PathParam( "id" ) String id, @PathParam( "type" ) String type ) {
    if ( repositoryBrowserController.loadFile( id, type ) ) {
      return Response.ok().build();
    }

    return Response.noContent().build();
  }

  @GET
  @Path( "/loadFiles/{path}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadFile( @PathParam( "path" ) String path ) {
    RepositoryDirectory repositoryDirectory = repositoryBrowserController.loadFiles( path );
    return Response.ok( repositoryDirectory ).build();
  }

  @GET
  @Path( "/loadFolders/{path}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadFolders( @PathParam( "path" ) String path ) {
    RepositoryDirectory repositoryDirectory = repositoryBrowserController.loadFolders( path );
    return Response.ok( repositoryDirectory ).build();
  }

  @GET
  @Path( "/loadFilesAndFolders/{path}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadFilesAndFolders( @PathParam( "path" ) String path ) {
    RepositoryDirectory repositoryDirectory = repositoryBrowserController.loadFilesAndFolders( path );
    return Response.ok( repositoryDirectory ).build();
  }

  @GET
  @Path( "/getActiveFileName" )
  public Response getActiveFileName() {
    String name = repositoryBrowserController.getActiveFileName();
    return Response.ok( Collections.singletonMap( "fileName", name ) ).build();
  }

  @GET
  @Path( "/loadRecent/{repo}/{id}" )
  public Response loadRecent( @PathParam( "repo" ) String repo, @PathParam( "id" ) String id ) {
    if ( repositoryBrowserController.openRecentFile( repo, id ) ) {
      return Response.ok().build();
    }
    return Response.status( Response.Status.NOT_FOUND ).build();
  }

  @GET
  @Path( "/saveFile/{path}/{name}/{fileName}/{override}" )
  public Response saveFile( @PathParam( "path" ) String path, @PathParam( "name" ) String name,
                            @PathParam( "fileName" ) String fileName,
                            @PathParam( "override" ) String override ) {
    boolean overwrite = override != null && override.toLowerCase().equals( "true" );
    if ( repositoryBrowserController.saveFile( path, name, fileName, overwrite ) ) {
      return Response.ok().build();
    }
    return Response.noContent().build();
  }

  @GET
  @Path( "/saveFile/{path}/{name}/{override}" )
  public Response saveFile( @PathParam( "path" ) String path, @PathParam( "name" ) String name,
                            @PathParam( "override" ) String override ) {
    boolean overwrite = override != null && override.toLowerCase().equals( "true" );
    if ( repositoryBrowserController.saveFile( path, name, "", overwrite ) ) {
      return Response.ok().build();
    }
    return Response.noContent().build();
  }

  @GET
  @Path( "/checkForSecurityOrDupeIssues/{path}/{name}/{fileName}/{override}" )
  public Response checkForSecurityOrDupeIssues( @PathParam( "path" ) String path, @PathParam( "name" ) String name,
                                                @PathParam( "fileName" ) String fileName,
                                                @PathParam( "override" ) String override ) {
    boolean overwrite = override != null && override.toLowerCase().equals( "true" );
    if ( repositoryBrowserController.checkForSecurityOrDupeIssues( path, name, fileName, overwrite ) ) {
      return Response.ok().build();
    }
    return Response.noContent().build();
  }

  @GET
  @Path( "/checkForSecurityOrDupeIssues/{path}/{name}/{override}" )
  public Response checkForSecurityOrDupeIssues( @PathParam( "path" ) String path, @PathParam( "name" ) String name,
                                                @PathParam( "override" ) String override ) {
    boolean overwrite = override != null && override.toLowerCase().equals( "true" );
    if ( repositoryBrowserController.checkForSecurityOrDupeIssues( path, name, "", overwrite ) ) {
      return Response.ok().build();
    }
    return Response.noContent().build();
  }

  @POST
  @Path( "/rename/{id}/{path}/{newName}/{type}/{oldName}" )
  public Response rename( @PathParam( "id" ) String id, @PathParam( "path" ) String path,
                          @PathParam( "newName" ) String newName, @PathParam( "type" ) String type,
                          @PathParam( "oldName" ) String oldName ) {

    try {
      ObjectId objectId = repositoryBrowserController.rename( id, path, newName, type, oldName );
      if ( objectId != null ) {
        return Response.ok( objectId ).build();
      }
    } catch ( KettleObjectExistsException koee ) {
      return Response.status( Response.Status.CONFLICT ).build();
    } catch ( KettleTransException | KettleJobException ktje ) {
      return Response.status( Response.Status.NOT_ACCEPTABLE ).build();
    } catch ( KettleException ke ) {
      return Response.notModified().build();
    }

    return Response.notModified().build();
  }

  @GET
  @Path( "/search/{path}/{filter}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response search( @PathParam( "path" ) String path, @PathParam( "filter" ) String filter ) {
    return Response.ok( repositoryBrowserController.search( path, filter ) ).build();
  }

  @POST
  @Path( "/create/{parent}/{name}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response rename( @PathParam( "parent" ) String parent, @PathParam( "name" ) String name ) {
    RepositoryDirectory repositoryDirectory = repositoryBrowserController.create( parent, name );
    if ( repositoryDirectory != null ) {
      return Response.ok( repositoryDirectory ).build();
    }

    return Response.status( Response.Status.UNAUTHORIZED ).build();
  }

  @DELETE
  @Path( "/remove/{id}/{name}/{path}/{type}" )
  public Response delete( @PathParam( "id" ) String id, @PathParam( "name" ) String name,
                          @PathParam( "path" ) String path, @PathParam( "type" ) String type ) {
    try {
      if ( repositoryBrowserController.remove( id, name, path, type ) ) {
        return Response.ok().build();
      }
    } catch ( KettleException ke ) {
      return Response.status( Response.Status.NOT_ACCEPTABLE ).build();
    }
    return Response.status( Response.Status.NOT_MODIFIED ).build();
  }

  @GET
  @Path( "/recentFiles" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response recentFiles() {
    return Response.ok( repositoryBrowserController.getRecentFiles() ).build();
  }

  @GET
  @Path( "/updateRecentFiles/{oldPath}/{newPath}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response updateRecentFiles( @PathParam( "oldPath" ) String oldPath, @PathParam( "newPath" ) String newPath ) {
    return Response.ok( repositoryBrowserController.updateRecentFiles( oldPath, newPath ) ).build();
  }

  @GET
  @Path( "/recentSearches" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response recentSearches() {
    return Response.ok( repositoryBrowserController.getRecentSearches() ).build();
  }

  @GET
  @Path( "/storeRecentSearch/{recentSearch}" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response storeRecentSearch( @PathParam( "recentSearch" ) String recentSearch ) {
    return Response.ok( repositoryBrowserController.storeRecentSearch( recentSearch ) ).build();
  }

  @GET
  @Path( "/currentRepo" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response getCurrentRepo() {
    return Response.ok( repositoryBrowserController.getCurrentRepo() ).build();
  }
}
