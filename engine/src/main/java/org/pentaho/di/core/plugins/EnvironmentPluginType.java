package org.pentaho.di.core.plugins;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.EnvironmentPlugin;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.environment.Environment;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Created by bmorrise on 11/12/18.
 */
@PluginMainClassType( Environment.class )
@PluginAnnotationType( EnvironmentPlugin.class )
public class EnvironmentPluginType extends BasePluginType implements PluginTypeInterface {

  private static EnvironmentPluginType environmentPluginType;

  private EnvironmentPluginType() {
    super( EnvironmentPlugin.class, "ENVIRONMENT_PLUGIN", "Environment Plugin" );
    populateFolders( "environments" );
  }

  public static EnvironmentPluginType getInstance() {
    if ( environmentPluginType == null ) {
      environmentPluginType = new EnvironmentPluginType();
    }
    return environmentPluginType;
  }
  @Override
  protected void registerXmlPlugins() throws KettlePluginException {

  }

  @Override
  protected List<JarFileAnnotationPlugin> findAnnotatedClassFiles( String annotationClassName ) {
    return super.findAnnotatedClassFiles( annotationClassName );
  }

  @Override
  protected String extractID( Annotation annotation ) {
    return ((EnvironmentPlugin) annotation).id();
  }

  @Override
  protected String getXmlPluginFile() {
    return Const.XML_FILE_KETTLE_ENGINES;
  }

  @Override
  protected String extractName( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractDesc( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractCategory( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractImageFile( Annotation annotation ) {
    return null;
  }

  @Override
  protected boolean extractSeparateClassLoader( Annotation annotation ) {
    return false;
  }

  @Override
  protected String extractI18nPackageName( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractDocumentationUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractSuggestion( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractCasesUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected String extractForumUrl( Annotation annotation ) {
    return null;
  }

  @Override
  protected void addExtraClasses( Map<Class<?>, String> classMap, Class<?> clazz, Annotation annotation ) {

  }

  @Override
  protected boolean isReturn() {
    return true;
  }
}
