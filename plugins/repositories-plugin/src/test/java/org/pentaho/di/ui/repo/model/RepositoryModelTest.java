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

package org.pentaho.di.ui.repo.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.ui.repo.handler.RepositoryMenuHandler;
import org.pentaho.di.ui.repo.ui.RepositoryMenuItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by bmorrise on 5/23/16.
 */
@RunWith( MockitoJUnitRunner.class )
public class RepositoryModelTest {

  private static final String REPOSITORY_ONE = "REPOSITORY ONE";

  private RepositoryModel repositoryModel;

  @Before
  public void setUp() {
    repositoryModel = new RepositoryModel();
  }

  @Test
  public void testGetRepositoryItems() {
    List<String> repositories = new ArrayList<>();
    repositories.add( REPOSITORY_ONE );

    repositoryModel.setRepositories( repositories );
    List<RepositoryMenuItem> repositoryMenuItems = repositoryModel.getRepositoryItems();
    for ( RepositoryMenuItem repositoryMenuItem : repositoryMenuItems ) {
      assertEquals( repositoryMenuItem.getCommand(),
        RepositoryMenuHandler.HANDLER_NAME + ".doLogin(\"" + REPOSITORY_ONE + "\")" );
      assertEquals( repositoryMenuItem.getLabel(), REPOSITORY_ONE );
      assertEquals( repositoryMenuItem.getSelected(), false );
    }
  }

}
