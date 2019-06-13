package org.pentaho.di.connections.common;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.InputStream;

public class TestFileObject extends AbstractFileObject<TestFileSystem> {

  public TestFileObject( AbstractFileName name,
                         TestFileSystem fs ) {
    super( name, fs );
  }

  @Override public boolean exists() throws FileSystemException {
    return true;
  }

  @Override protected long doGetContentSize() throws Exception {
    return 0;
  }

  @Override protected InputStream doGetInputStream() throws Exception {
    return null;
  }

  @Override protected FileType doGetType() throws Exception {
    return FileType.FILE;
  }

  @Override protected String[] doListChildren() throws Exception {
    return new String[ 0 ];
  }
}
