package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.quicksearch.ui.util.Images;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/20/18.
 */
public class FileResult implements SearchResult {

  private Image TRANS_IMAGE = Images.getImage( FileResult.class, "transformation.svg", 24 );
  private Image JOB_IMAGE = Images.getImage( FileResult.class, "job.svg", 24 );

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  public static final String KTR = "ktr";

  public static String FILE = "file";
  public static String DIRECTORY = "directory";

  private String id;
  private String path;
  private String name;
  private String type;
  private String extension;

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension( String extension ) {
    this.extension = extension;
  }

  @Override
  public String getDescription() {
    return path;
  }

  @Override
  public void execute() {
    openFile( id, extension );
  }

  @Override
  public Image getImage() {
    return extension.equals( "ktr" ) ? TRANS_IMAGE : JOB_IMAGE;
  }

  private void openFile( String id, String extension ) {
    if ( spoonSupplier.get() != null ) {
      spoonSupplier.get().getDisplay().asyncExec( () -> {
        try {
          spoonSupplier.get().loadObjectFromRepository( () -> id, extension.equals( KTR ) ? RepositoryObjectType
                  .TRANSFORMATION : RepositoryObjectType.JOB, null );
        } catch ( Exception e ) {
          // Error
        }
      } );
    }
  }
}
