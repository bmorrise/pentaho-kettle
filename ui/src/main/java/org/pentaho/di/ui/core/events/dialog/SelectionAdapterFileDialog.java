package org.pentaho.di.ui.core.events.dialog;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.local.LocalFileSystem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.ui.core.FileDialogOperation;
import org.pentaho.di.ui.core.events.dialog.extension.ExtensionPointWrapper;
import org.pentaho.di.ui.core.events.dialog.extension.SpoonOpenExtensionPointWrapper;

import java.util.Arrays;

/**
 * This adapter class opens up the VFS file dialog, where the primary goal is to select a file.
 *
 * This adapter class provides default implementations for the methods described by the SelectionListener interface.
 *
 * Classes that wish to deal with SelectionEvents can extend this class and override only the methods which they are
 * interested in.
 *
 * example use:
 *
 *  Dialog.java
 *
 *      wbFilename.addSelectionListener(  new SelectionAdapterFileDialog( log, wFilename, transMeta,
 *          SelectionOperation.FILE, new String[] { "TXT", "ALL" } ) );
 *
 *
 * Side effect:
 *  wFilename#setText() will be called with chosen file.
 *
 */
public abstract class SelectionAdapterFileDialog<T> extends SelectionAdapter {
  private final LogChannelInterface log;
  private final T textWidget;
  private final AbstractMeta meta;
  private final SelectionOperation selectionOperation;
  private final String[] filter;
  private final RepositoryUtility repositoryUtility;
  private final ExtensionPointWrapper extensionPointWrapper;

  /**
   * Filter option to allow all file types.
   * Defined in javascript section of code in json file.
   */
  public static final String FILTER_ALL = "ALL";

  public SelectionAdapterFileDialog( LogChannelInterface log, T textWidget, AbstractMeta meta,
                                     SelectionOperation selectionOperation, String[] filter,
                                     RepositoryUtility repositoryUtility, ExtensionPointWrapper extensionPointWrapper  ) {
    this.log = log;
    this.textWidget = textWidget;
    this.meta = meta;
    this.selectionOperation = selectionOperation;
    this.filter = filter;
    this.repositoryUtility = repositoryUtility;
    this.extensionPointWrapper = extensionPointWrapper;
  }

  public SelectionAdapterFileDialog( LogChannelInterface log, T textWidget, AbstractMeta meta,
                                     SelectionOperation selectionOperation, String[] filter) {
    this( log, textWidget, meta, selectionOperation, filter,
      new RepositoryUtility(), new SpoonOpenExtensionPointWrapper() );
  }

  @Override
  public void widgetSelected( SelectionEvent selectionEvent ) {
    super.widgetSelected( selectionEvent );
    widgetSelectedHelper( selectionEvent );
  }

  /**
   * Get underlying widget that will get text assigned.
   * @return
   */
  public T getTextWidget() {
    return this.textWidget;
  }

  /**
   * Get text of widget.
   * @return text from widget.
   */
  abstract protected String getText();

  /**
   * Set text for widget.
   * @param text
   */
  abstract protected void setText(String text);

  protected void widgetSelectedHelper( SelectionEvent selectionEvent ) {
    FileDialogOperation fileDialogOperation;
    FileObject selectedFile = null;
    //TODO do we want to preserve the variables in the textVar ??

    if ( getText() != null ) {

      try {
        //set Up initial conditions
        FileObject initialFile = resolveFile( meta, getText() );
        fileDialogOperation = constructFileDialogOperation(selectionOperation, initialFile, filter );

        // open dialog
        extensionPointWrapper.callExtensionPoint( log, KettleExtensionPoint.SpoonOpenSaveNew.id, fileDialogOperation );

        // grab path
        String path = constructPath( fileDialogOperation );

        // FIXME is the returned path going to still have vfs schema
        // TODO HERE should we keep the original path with the unresolved variables/parameters
        selectedFile = getFileObject( path );
      } catch ( KettleFileException kfe ) {
        log.logError( "Error in widgetSelectedHelper", kfe );
      }
      catch ( KettleException ke ) {
        log.logError( "Error in widgetSelectedHelper", ke );
      }
    }

    if ( selectedFile != null ) {
      String file = selectedFile.getName().getURI();
      if ( !StringUtils.isBlank( file ) ) {
        // TODO make this /C: replace for all lettered drives
        file = file.replace( "file://", "" ).replace( "/C:", "C:" );
      }
      if ( !file.contains( System.getProperty( "file.separator" ) ) ) {
        if ( !System.getProperty( "file.separator" ).equals( "/" ) && !Const.isWindows() ) {
          file = file.replace( "/", System.getProperty( "file.separator" ) );
        }
      }
      setText( file );
    }
  }

  protected FileDialogOperation constructFileDialogOperation( SelectionOperation selectionOperation,
                                                              FileObject initialFile, String[] filter ) throws KettleException {
    FileDialogOperation fileDialogOperation = createFileDialogOperation( selectionOperation );
    setProvider( fileDialogOperation, initialFile );
    setPath( fileDialogOperation, initialFile );
    setStartDir( fileDialogOperation, initialFile );
    setFilter( fileDialogOperation, filter );
    setFilename( fileDialogOperation, initialFile );
    // fields title, fileType not used by VFS

    return fileDialogOperation;
  }

  protected FileObject getFileObject( String path) {
    FileObject fileObject;
    try {
      fileObject = KettleVFS.getFileObject( path );
    }
    catch ( Exception e) {
      fileObject = null;
    }
    return fileObject;
  }

  protected FileObject resolveFile( AbstractMeta abstractMeta, String unresolvedPath ) throws KettleFileException {
    return KettleVFS.getFileObject( abstractMeta.environmentSubstitute( unresolvedPath ) );
  }

  protected FileDialogOperation createFileDialogOperation( SelectionOperation selectionOperation ) {
    String selectOperation = selectionOperation == SelectionOperation.FILE
        ? FileDialogOperation.SELECT_FILE
        : FileDialogOperation.SELECT_FOLDER;
    return new FileDialogOperation( selectOperation, FileDialogOperation.ORIGIN_SPOON );
  }

  protected void setPath( FileDialogOperation fileDialogOperation, FileObject fileObject ) throws KettleException {
    try {
      String vfsPath = fileObject.isFile()
        ? fileObject.toString()
        : null;
      fileDialogOperation.setPath( removeVFSFileScheme( vfsPath ) );
    }
    catch ( FileSystemException fse ) {
      throw new KettleException( "failed to check isFile in setPath()", fse );
    }
  }

  protected void setStartDir( FileDialogOperation fileDialogOperation, FileObject fileObject ) throws KettleException {
    try {
      String vfsPath = fileObject.isFile()
        ? null
        : fileObject.toString();
      fileDialogOperation.setStartDir( removeVFSFileScheme( vfsPath ) );
    }
    catch ( FileSystemException fse ) {
      throw new KettleException( "failed to check isFile in setStartDir()", fse );
    }
  }

  protected void setFilename( FileDialogOperation fileDialogOperation, FileObject fileObject ) {
    fileDialogOperation.setFilename( fileObject.getName().toString() );
  }

  protected String removeVFSFileScheme( String vfsPath ) {
    return vfsPath != null
      ? vfsPath.replace( "file://", "" )
      : null;
  }

  protected void setProvider( FileDialogOperation fileDialogOperation, FileObject fileObject ) {
    String provider;
    if ( isConnectedToRepository() ) {
      provider = "repository"; // TODO follow up with been, only setProvider if connected to repo, don't set connection
    } else if ( fileObject.getFileSystem() instanceof LocalFileSystem ) {
      provider = "local"; // TODO manage constants better - talk with BEN
    } else if ( fileObject.getFileSystem() instanceof AbstractFileSystem ) {
      provider = "vfs";
    } else {
      provider = null;
      // TODO does this accommodate big-data-plugin
      /*
       * NOTE: null on purpose, sub-class should override function
       * and return new class that is in sync with ExtensionPoint
       */
    }
    fileDialogOperation.setProvider( provider );
  }

  /**
   * Helper function for {@link FileDialogOperation#setFilter} . Blank entries in <code>filters</code> will be removed.
   * If an "blank" array is entered, the less restrictive filters option {@link FILTER_ALL} will be applied.
   * @param fileDialogOperation
   * @param filters
   */
  protected void setFilter( FileDialogOperation fileDialogOperation, String[] filters ) {
    String[] cleanedFilters = cleanFilters( filters );
    String filterString = ArrayUtils.isEmpty( cleanedFilters )
        ? FILTER_ALL // least restrictive option
        : String.join( ",", cleanedFilters );

    fileDialogOperation.setFilter( filterString );
  }

  /**
   * Remove "blank" items such as empty, null or whitespace items in <code>filters</code>
   * @param filters
   * @return non "blank" array.
   */
  protected String[] cleanFilters(String[] filters) {
    return !ArrayUtils.isEmpty( filters )
        ? Arrays.asList( filters ).stream().filter( f -> !StringUtils.isBlank( f ) ).toArray(String[]::new)
        : null;
  }

  /**
   * Determine if connected to repository.
   * @return true if connected, false otherwise.
   */
  protected boolean isConnectedToRepository() {
    return this.repositoryUtility.isConnectedToRepository();
  }

  protected String constructPath( FileDialogOperation fileDialogOperation ) {
    String path;
    try {
      path = isProviderRepository( fileDialogOperation )
        ? getRepositoryFilePath( fileDialogOperation )
        // TODO does this work for provider=vfs
        : getFilePath( fileDialogOperation );
    } catch (Exception e) {
      path = null;
    }
    return path;
  }

  protected String getRepositoryFilePath( FileDialogOperation fileDialogOperation ) {
    return getRepositoryFilePath( (RepositoryElementMetaInterface) fileDialogOperation.getRepositoryObject() );
  }

  /**
   * construct <code>repositoryElementMeta</code> path. Similar to java.io.File method 'getPath()'
   * @param repositoryElementMeta
   * @return
   */
  protected String getRepositoryFilePath( RepositoryElementMetaInterface repositoryElementMeta ) {
    return concat( repositoryElementMeta.getRepositoryDirectory().getPath(), repositoryElementMeta.getName() );
  }

  protected String getFilePath( FileDialogOperation fileDialogOperation ) {
    return concat( fileDialogOperation.getPath(), fileDialogOperation.getFilename() );
  }

  protected String concat( String path, String name ) {
    return FilenameUtils.concat( path, name );
  }

  //TODO move to FileDialogOperation.java
  protected boolean isProviderFile( FileDialogOperation fileDialogOperation ) {
    return fileDialogOperation.getProvider() != null
        && fileDialogOperation.getProvider().equalsIgnoreCase( "transformation" );
    //FIXME FileOpenSaveExtensionPoint."TRANSFORMATION" should be changed to
  }

  //TODO move to FileDialogOperation.java
  protected boolean isProviderRepository( FileDialogOperation fileDialogOperation ) {
    /**
     // FIXME Me this really be the logic
    return fileDialogOperation.getProvider() != null
      && fileDialogOperation.getProvider().equalsIgnoreCase( "repository" );
     **/
    return fileDialogOperation.getRepositoryObject() != null;
  }

}
