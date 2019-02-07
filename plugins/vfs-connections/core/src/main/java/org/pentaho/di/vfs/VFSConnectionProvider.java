package org.pentaho.di.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.vfs.model.FieldMeta;

import java.util.List;

/**
 * Created by bmorrise on 2/3/19.
 */
public interface VFSConnectionProvider<T extends VFSConnectionDetails> {
  String getName();
  String getSchema();
  Class<T> getType();
  FileSystemOptions getOpts( T vfsConnectionDetails );
  String getTemplate();
  List<FieldMeta> getFields();
}
