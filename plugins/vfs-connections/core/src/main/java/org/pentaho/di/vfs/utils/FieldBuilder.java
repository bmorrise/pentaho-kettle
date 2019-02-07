package org.pentaho.di.vfs.utils;

import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.vfs.VFSConnectionDetails;
import org.pentaho.di.vfs.annotation.FieldMetaType;
import org.pentaho.di.vfs.model.FieldMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/7/19.
 */
public class FieldBuilder {

  public List<FieldMeta> build( Class<? extends  VFSConnectionDetails> clazz ) {
    List<FieldMeta> fieldMetas = new ArrayList<>();
    for ( Field field : clazz.getDeclaredFields() ) {
      for ( Annotation annotation : field.getAnnotations() ) {
        if ( annotation instanceof FieldMetaType ) {
          FieldMetaType fieldMetaType = (FieldMetaType) annotation;
          FieldMeta fieldMeta = new FieldMeta( field.getName(), getTranslated( clazz, fieldMetaType.label() ) );
          fieldMetas.add( fieldMeta );
        }
      }
    }
    return fieldMetas;
  }

  private String getTranslated( Class<? extends  VFSConnectionDetails> clazz, String key ) {
    String name = clazz.getSimpleName() + ".Label." + key;
    return BaseMessages.getString( clazz, name );
  }

}
