package org.pentaho.di.vfs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bmorrise on 2/7/19.
 */
@Retention( RetentionPolicy.RUNTIME)
@Target( ElementType.FIELD)
public @interface FieldMetaType {
  String label();
}
