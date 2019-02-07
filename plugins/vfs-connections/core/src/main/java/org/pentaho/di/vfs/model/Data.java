package org.pentaho.di.vfs.model;

import org.pentaho.di.vfs.VFSConnectionDetails;

import java.util.List;
import java.util.Map;

/**
 * Created by bmorrise on 2/5/19.
 */
public class Data {
  private VFSConnectionDetails model;
  private List<FieldMeta> fields;
  private String type;
  private String template;

  public Data() {
  }

  public Data( VFSConnectionDetails model, String template, List<FieldMeta> fields, String type ) {
    this.model = model;
    this.template = template;
    this.fields = fields;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public List<FieldMeta> getFields() {
    return fields;
  }

  public void setFields( List<FieldMeta> fields ) {
    this.fields = fields;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate( String template ) {
    this.template = template;
  }

  public VFSConnectionDetails getModel() {
    return model;
  }

  public void setModel( VFSConnectionDetails model ) {
    this.model = model;
  }
}
