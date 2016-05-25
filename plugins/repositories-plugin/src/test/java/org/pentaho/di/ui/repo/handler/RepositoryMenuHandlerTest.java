/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
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

package org.pentaho.di.ui.repo.handler;

import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.repo.RepositoryDialog;
import org.pentaho.di.ui.repo.RepositoryDialogFactory;
import org.pentaho.di.ui.repo.controller.RepositoryController;
import org.pentaho.di.ui.repo.model.RepositoryModel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bmorrise on 5/23/16.
 */
@RunWith( MockitoJUnitRunner.class )
public class RepositoryMenuHandlerTest {

  private static final String REPOSITORY_NAME = "Repository Name";
  private static final String KETTLE_FILE_REPOSITORY = "KettleFileRepository";
  private static final String KETTLE_DATABASE_REPOSITORY = "KettleDatabaseRepository";

  @Mock private RepositoryController repositoryController;
  @Mock private RepositoryModel repositoryModel;
  @Mock private RepositoryDialogFactory repositoryDialogFactory;
  @Mock private RepositoryMeta repositoryMeta;
  @Mock private RepositoryDialog repositoryDialog;

  private RepositoryMenuHandler repositoryMenuHandler;

  @BeforeClass
  public static void setUpClass() throws Exception {
    if ( !KettleEnvironment.isInitialized() ) {
      KettleEnvironment.init();
    }
    if ( !PropsUI.isInitialized() ) {
      PropsUI.init( new Display(), Props.TYPE_PROPERTIES_SPOON );
    }
  }

  @Before
  public void setUp() {
    repositoryMenuHandler =
      new RepositoryMenuHandler( repositoryController, repositoryModel, repositoryDialogFactory );
  }

  @Test
  public void testDoLogin() {
    when( repositoryController.getRepositoryMeta( REPOSITORY_NAME ) ).thenReturn( repositoryMeta );
    when( repositoryMeta.getId() ).thenReturn( KETTLE_FILE_REPOSITORY );

    repositoryMenuHandler.doLogin( REPOSITORY_NAME );

    verify( repositoryController ).connectToRepository( repositoryMeta );

    when( repositoryMeta.getId() ).thenReturn( KETTLE_DATABASE_REPOSITORY );
    when( repositoryDialogFactory.getDialog() ).thenReturn( repositoryDialog );

    repositoryMenuHandler.doLogin( REPOSITORY_NAME );
    verify( repositoryModel ).fireBindings();
    verify( repositoryDialogFactory ).getDialog();
    verify( repositoryDialog ).openLogin( repositoryMeta );
  }

}
