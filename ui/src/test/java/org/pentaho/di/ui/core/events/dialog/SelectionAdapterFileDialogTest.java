package org.pentaho.di.ui.core.events.dialog;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectInterface;
import org.pentaho.di.ui.core.FileDialogOperation;
import org.pentaho.di.ui.core.events.dialog.extension.ExtensionPointWrapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pentaho.di.ui.core.events.dialog.SelectionAdapterFileDialog.FILTER_ALL;

public class SelectionAdapterFileDialogTest {

  SelectionAdapterFileDialog testInstance;

  @Before
  public void setup() {
    testInstance = createTestInstance();
  }

  @Test
  public void testWidgetSelected() {
  }

  @Test
  public void testWidgetSelectedHelper() {
  }

  @Test
  public void testResolveFile() throws Exception {
    String unresolvedPath = "{SOME_VAR}/some/path";
    String resolvedPath = "/home/devuser/some/path";
    AbstractMeta abstractMeta = mock( AbstractMeta.class );
    when( abstractMeta.environmentSubstitute( unresolvedPath ) ).thenReturn( resolvedPath );

    assertNotNull( testInstance.resolveFile( abstractMeta, unresolvedPath ) );
  }

  @Test
  public void testResolveFile1() {
  }

  @Test
  public void testCreateFileDialogOperation() {
    // TEST : SELECT file
    FileDialogOperation fdo1 = testInstance.createFileDialogOperation( SelectionOperation.FILE );
    assertNotNull( fdo1 );
    assertEquals( FileDialogOperation.SELECT_FILE, fdo1.getCommand() );
    assertEquals( FileDialogOperation.ORIGIN_SPOON, fdo1.getOrigin() );

    // TEST : SELECT folder
    FileDialogOperation fdo2 = testInstance.createFileDialogOperation( SelectionOperation.FOLDER );
    assertNotNull( fdo2 );
    assertEquals( FileDialogOperation.SELECT_FOLDER, fdo2.getCommand() );
    assertEquals( FileDialogOperation.ORIGIN_SPOON, fdo2.getOrigin() );
  }

  @Test
  public void testSetPath() throws Exception {
    // TEST : is file
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    FileObject fileObject1 = mock( FileObject.class );
    String absoluteFilePath = "/home/someuser/somedir";
    when( fileObject1.isFile() ).thenReturn( true );
    when( fileObject1.toString() ).thenReturn( absoluteFilePath );

    testInstance.setPath( fileDialogOperation1, fileObject1 );

    assertEquals( absoluteFilePath, fileDialogOperation1.getPath() );

    // TEST : is not file
    FileDialogOperation fileDialogOperation2 = createFileDialogOperation();
    FileObject fileObject2 = mock( FileObject.class );
    when( fileObject2.isFile() ).thenReturn( false );

    testInstance.setPath( fileDialogOperation2, fileObject2 );

    assertNull( fileDialogOperation2.getPath() );
  }

  @Test(expected = KettleException.class)
  public void testSetPath_Exception() throws Exception {
    // TEST : is file
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    FileObject fileObject1 = mock( FileObject.class );
    when( fileObject1.isFile() ).thenThrow(  new FileSystemException( "some error" ) );

    testInstance.setPath( fileDialogOperation1, fileObject1 );
  }

  @Test
  public void testSetStartDir() throws Exception {
    // TEST : is file
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    FileObject fileObject1 = mock( FileObject.class );
    when( fileObject1.isFile() ).thenReturn( true );

    testInstance.setStartDir( fileDialogOperation1, fileObject1 );

    assertNull( fileDialogOperation1.getStartDir() );

    // TEST : is not file
    FileDialogOperation fileDialogOperation2 = createFileDialogOperation();
    FileObject fileObject2 = mock( FileObject.class );
    String absoluteFilePath = "/home/someuser/somedir";
    when( fileObject2.isFile() ).thenReturn( false );
    when( fileObject2.toString() ).thenReturn( absoluteFilePath );

    testInstance.setStartDir( fileDialogOperation2, fileObject2 );

    assertEquals( absoluteFilePath, fileDialogOperation2.getStartDir() );
  }

  @Test(expected = KettleException.class)
  public void testSetStartDir_Exception() throws Exception {
    // TEST : is file
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    FileObject fileObject1 = mock( FileObject.class );
    when( fileObject1.isFile() ).thenThrow(  new FileSystemException( "some error" ) );

    testInstance.setStartDir( fileDialogOperation1, fileObject1 );
  }

  @Test
  public void testRemoveVFSFileScheme() {

    // TEST 1: null path
    assertNull( testInstance.removeVFSFileScheme( null ) );

    // TEST 2: no vfs prefix
    String path2 = "/home/devuser/files/food.txt";
    assertEquals( path2, testInstance.removeVFSFileScheme( path2 ) );

    // TEST 3: vfs zip path
    String path3 = "jar:zip:outer.zip!/nested.jar!/somedir";
    assertEquals( path3, testInstance.removeVFSFileScheme( path3 ) );

    // TEST 4: vfs file path
    String expectedPath4 = "/home/someuser/somedir";
    String path4 = "file://" + expectedPath4;
    assertEquals( expectedPath4, testInstance.removeVFSFileScheme( path4 ) );
  }

  @Test
  public void testSetProvider() {
  }

  @Test
  public void testIsProviderRepository() {
  }

  @Test
  public void testGetRepositoryFilePath() {
    RepositoryDirectoryInterface repositoryDirectory = mock( RepositoryDirectoryInterface.class);
    when ( repositoryDirectory.getPath() ).thenReturn( "/home/devuser/files" );
    RepositoryObject repositoryObject = mock( RepositoryObject.class );
    when ( repositoryObject.getRepositoryDirectory() ).thenReturn( repositoryDirectory );
    when ( repositoryObject.getName() ).thenReturn( "food.txt" );
    FileDialogOperation fileDialogOperation = createFileDialogOperation();
    fileDialogOperation.setRepositoryObject( repositoryObject );

    assertEquals( "/home/devuser/files/food.txt", testInstance.getRepositoryFilePath( fileDialogOperation  ) );
  }

  @Test
  public void testConcat() {

    assertEquals("/home/devuser/files/food.txt",
      testInstance.concat("/home/devuser/files", "food.txt" ) );

    assertEquals("/home/devuser/files/food.txt",
      testInstance.concat("/home/devuser/files/", "food.txt" ) );

    assertEquals("/home/devuser/files/food.txt",
      testInstance.concat("/", "home/devuser/files/food.txt" ) );

    assertEquals("/",
      testInstance.concat("/", "" ) );

    assertEquals("/home/devuser/files/",
      testInstance.concat("/home/devuser/files", "" ) );
  }

  @Test
  public void testIsProviderFile() {
  }

  @Test
  public void testGetFilePath() {
    FileDialogOperation fileDialogOperation = createFileDialogOperation();
    fileDialogOperation.setPath( "/home/devuser/files" );
    fileDialogOperation.setFilename( "food.txt" );

    assertEquals( "/home/devuser/files/food.txt", testInstance.getFilePath( fileDialogOperation ) );
  }

  @Test
  public void testSetFilename() {
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    FileObject fileObject1 = mock( FileObject.class );
    String path = "/home/devuser/files/food.txt";
    FileName filename = mock( FileName.class );
    when( filename.toString() ).thenReturn( path );
    when( fileObject1.getName()).thenReturn( filename );

    testInstance.setFilename( fileDialogOperation1, fileObject1 );

    assertEquals( path, fileDialogOperation1.getFilename() );
  }

  @Test
  public void testSetFilter() {

    // TEST : null filter
    FileDialogOperation fileDialogOperation1 = createFileDialogOperation();
    testInstance.setFilter( fileDialogOperation1, null );

    assertEquals( FILTER_ALL, fileDialogOperation1.getFilter() );

    // TEST: empty array filter
    FileDialogOperation fileDialogOperation2 = createFileDialogOperation();
    testInstance.setFilter( fileDialogOperation2, new String[]{} );

    assertEquals( FILTER_ALL, fileDialogOperation2.getFilter() );

    // TEST : one item filter
    FileDialogOperation fileDialogOperation3 = createFileDialogOperation();
    testInstance.setFilter( fileDialogOperation3, new String[]{"TXT"} );

    assertEquals( "TXT", fileDialogOperation3.getFilter() );

    // TEST : multiple filters
    FileDialogOperation fileDialogOperation4 = createFileDialogOperation();
    testInstance.setFilter( fileDialogOperation4, new String[]{"TXT","CSV","XML"} );

    assertEquals( "TXT,CSV,XML" , fileDialogOperation4.getFilter() );
  }

  @Test
  public void testCleanFilters() {

    String[] EMPTY_ARRAY = new String[]{};

    assertNull( testInstance.cleanFilters( null ) );

    assertNull( testInstance.cleanFilters( new String[]{} ) );

    assertArrayEquals( EMPTY_ARRAY, testInstance.cleanFilters( new String[]{ null } ) );

    assertArrayEquals( EMPTY_ARRAY, testInstance.cleanFilters( new String[]{ null, null, null } ) );

    assertArrayEquals( EMPTY_ARRAY, testInstance.cleanFilters( new String[]{ "     ", null, "" } ) );

    assertArrayEquals( new String[]{ "TXT", "CSV" }, testInstance.cleanFilters( new String[]{ "TXT", null, "CSV", "" } ) );

    assertArrayEquals( new String[]{ "TXT", "CSV" }, testInstance.cleanFilters( new String[]{ "TXT", "CSV" } ) );
  }

  @Test
  public void testIsConnectedToRepository() {

    //SETUP
    LogChannelInterface log = mock( LogChannelInterface.class );
    StringBuilder textVar = new StringBuilder();
    AbstractMeta meta = mock( AbstractMeta.class );
    ExtensionPointWrapper extensionPointWrapper = mock(ExtensionPointWrapper.class);
    SelectionOperation selectionOperation = SelectionOperation.FILE;

    // True case:
    RepositoryUtility repositoryUtilityTrue = mock( RepositoryUtility.class );
    when( repositoryUtilityTrue.isConnectedToRepository() ).thenReturn( true );

    SelectionAdapterFileDialog testInstance1 = createTestInstance( log, textVar, meta, selectionOperation, null,
      repositoryUtilityTrue, extensionPointWrapper );

    assertTrue( testInstance1.isConnectedToRepository() );

    // False case:
    RepositoryUtility repositoryUtilityFalse = mock( RepositoryUtility.class );
    when( repositoryUtilityFalse.isConnectedToRepository() ).thenReturn( false );

    SelectionAdapterFileDialog testInstance2 = createTestInstance( log, textVar, meta, selectionOperation, null,
      repositoryUtilityFalse, extensionPointWrapper );

    assertFalse( testInstance2.isConnectedToRepository() );
  }

  protected FileDialogOperation createFileDialogOperation() {
    return new FileDialogOperation( FileDialogOperation.SELECT_FILE, FileDialogOperation.ORIGIN_SPOON );
  }

  protected SelectionAdapterFileDialog createTestInstance() {
    LogChannelInterface log = mock( LogChannelInterface.class );
    StringBuilder textWidget = new StringBuilder();
    AbstractMeta meta = mock( AbstractMeta.class );
    SelectionOperation selectionOperation = SelectionOperation.FILE;
    RepositoryUtility repositoryUtility = mock( RepositoryUtility.class );
    ExtensionPointWrapper extensionPointWrapper = mock( ExtensionPointWrapper.class );

    return createTestInstance( log, textWidget, meta, selectionOperation, new String[]{}, repositoryUtility,
      extensionPointWrapper );
  }

  protected SelectionAdapterFileDialog createTestInstance( LogChannelInterface log, StringBuilder textWidget,
                                                           AbstractMeta meta, SelectionOperation selectionOperation,
                                                           String[] filter, RepositoryUtility repositoryUtility,
                                                           ExtensionPointWrapper extensionPointWrapper  ) {
    return new TestSelectionAdapterFileDialog( log, textWidget, meta, selectionOperation, filter,
      repositoryUtility, extensionPointWrapper );
  }

  /**
   * Test class to abstract class logic under test.
   */
  public static class TestSelectionAdapterFileDialog extends SelectionAdapterFileDialog<StringBuilder> {

    public StringBuilder textValue; // DO I NEED This
    public TestSelectionAdapterFileDialog( LogChannelInterface log, StringBuilder textWidget, AbstractMeta meta,
                                           SelectionOperation selectionOperation, String[] filter,
                                           RepositoryUtility repositoryUtility, ExtensionPointWrapper extensionPointWrapper  )  {
      super( log, textWidget, meta, selectionOperation, filter, repositoryUtility, extensionPointWrapper  );
    }

    @Override protected String getText() {
      return this.getTextWidget().toString();
    }

    @Override protected void setText( String text ) {
      this.getTextWidget().setLength( 0 );
      this.getTextWidget().append( text);
    }
  }
}