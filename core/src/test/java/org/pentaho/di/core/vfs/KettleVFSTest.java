/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.core.vfs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.URLFileNameParser;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class KettleVFSTest {

  /**
   * Test to validate that startsWitScheme() returns true if the fileName starts with
   * known protocol like zip: jar: then it returns true else returns false
   * @param fileName
   */
  @Test
  public void testStartsWithScheme() {
    String fileName = "zip:file:///SavedLinkedres.zip!Calculate median and percentiles using the group by steps.ktr";
    assertTrue( KettleVFS.startsWithScheme( fileName ) );

    fileName = "SavedLinkedres.zip!Calculate median and percentiles using the group by steps.ktr";
    assertFalse( KettleVFS.startsWithScheme( fileName ) );
  }

  @Test
  public void testMultipleProviders() throws Exception {
    KettleVFS kettleVFS = KettleVFS.getInstance();
    ((DefaultFileSystemManager) kettleVFS.getFileSystemManager()).addProvider( "test", new TestFileProvider() );

    FileSystemOptions fileSystemOptions1 = new FileSystemOptions();
    TestConfigBuilder testConfigBuilder1 = new TestConfigBuilder();
    testConfigBuilder1.setParam( fileSystemOptions1, "username", "username1" );
    testConfigBuilder1.setParam( fileSystemOptions1, "password", "password1" );
    KettleVFS.getFileObject( "test://file.test", fileSystemOptions1 );

    FileSystemOptions fileSystemOptions2 = new FileSystemOptions();
    TestConfigBuilder testConfigBuilder2 = new TestConfigBuilder();
    testConfigBuilder2.setParam( fileSystemOptions2, "username", "username2" );
    testConfigBuilder2.setParam( fileSystemOptions2, "password", "password2" );
    KettleVFS.getFileObject( "test://file.test", fileSystemOptions2 );
  }

  static class TestConfigBuilder extends FileSystemConfigBuilder {
    @Override
    protected Class<? extends FileSystem> getConfigClass() {
      return TestFileSystem.class;
    }

    @Override
    protected void setParam( FileSystemOptions opts, String name, Object value ) {
      super.setParam( opts, name, value );
    }

    @Override
    protected Object getParam( FileSystemOptions options, String name ) {
      return super.getParam( options, name );
    }
  }

  static class TestFileProvider extends AbstractOriginatingFileProvider {

    protected static final Collection<Capability>
            capabilities = Collections.unmodifiableCollection( Arrays.asList( Capability.CREATE, Capability.DELETE,
            Capability.RENAME, Capability.GET_TYPE, Capability.LIST_CHILDREN, Capability.READ_CONTENT, Capability.URI,
            Capability.WRITE_CONTENT, Capability.GET_LAST_MODIFIED, Capability.RANDOM_ACCESS_READ ) );


    public TestFileProvider() {
      setFileNameParser( new URLFileNameParser( 80 ) );
    }

    @Override
    protected FileSystem doCreateFileSystem( FileName fileName, FileSystemOptions fileSystemOptions ) throws FileSystemException {
      return new TestFileSystem( fileName, null, fileSystemOptions );
    }

    @Override
    public Collection<Capability> getCapabilities() {
      return capabilities;
    }
  }

  static class TestFileSystem extends AbstractFileSystem {
    public TestFileSystem( FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions ) {
      super( rootName, parentLayer, fileSystemOptions );
    }

    @Override
    protected FileObject createFile( AbstractFileName abstractFileName ) throws Exception {
      TestConfigBuilder testConfigBuilder = new TestConfigBuilder();
      String username = (String) testConfigBuilder.getParam( this.getFileSystemOptions(), "username" );
      String password = (String) testConfigBuilder.getParam( this.getFileSystemOptions(), "password" );
      System.out.println( username );
      System.out.println( password );
      return new TestFileObject( abstractFileName, this );
    }

    @Override
    protected void addCapabilities( Collection<Capability> collection ) {

    }
  }

  static class TestFileObject extends AbstractFileObject<TestFileSystem> {
    public TestFileObject( AbstractFileName name, TestFileSystem fs ) {
      super( name, fs );
    }

    @Override
    protected long doGetContentSize() throws Exception {
      return 0;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
      return null;
    }

    @Override
    protected FileType doGetType() throws Exception {
      return null;
    }

    @Override
    protected String[] doListChildren() throws Exception {
      return new String[0];
    }
  }
}
