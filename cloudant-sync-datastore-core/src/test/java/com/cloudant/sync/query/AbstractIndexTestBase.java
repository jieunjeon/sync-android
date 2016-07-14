//  Copyright (c) 2014 Cloudant. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
//  except in compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the
//  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
//  either express or implied. See the License for the specific language governing permissions
//  and limitations under the License.

package com.cloudant.sync.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreFaçade;
import com.cloudant.sync.datastore.DatastoreImpl;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.sqlite.SQLDatabase;
import com.cloudant.sync.util.SQLDatabaseTestUtils;
import com.cloudant.sync.util.TestUtils;

import org.junit.After;
import org.junit.Before;

import java.lang.reflect.Proxy;

public abstract class AbstractIndexTestBase {

    String factoryPath = null;
    DatastoreManager factory = null;
    DatastoreImpl ds = null;
    IndexManager im = null;
    SQLDatabase db = null;

    @Before
    public void setUp() throws Exception {
        factoryPath = TestUtils.createTempTestingDir(AbstractIndexTestBase.class.getName());
        assertThat(factoryPath, is(notNullValue()));
        factory = DatastoreManager.getInstance(factoryPath);
        assertThat(factory, is(notNullValue()));

        Datastore proxy = this.factory.openDatastore(getClass().getSimpleName());
        DatastoreFaçade datastoreFasçde = (DatastoreFaçade) Proxy.getInvocationHandler(proxy);
        this.ds = datastoreFasçde.getDatastoreImplementation();

        assertThat(ds, is(notNullValue()));
        im = new IndexManager(ds);
        assertThat(im, is(notNullValue()));
        db = TestUtils.getDatabaseConnectionToExistingDb(im.getDatabase());
        assertThat(db, is(notNullValue()));
        assertThat(im.getQueue(), is(notNullValue()));
        String[] metadataTableList = new String[] { IndexManager.INDEX_METADATA_TABLE_NAME };
        SQLDatabaseTestUtils.assertTablesExist(TestUtils.getDatabaseConnectionToExistingDb(db),
                                               metadataTableList);
    }

    @After
    public void tearDown() {
        im.close();
        assertThat(im.getQueue().isShutdown(), is(true));
        TestUtils.deleteDatabaseQuietly(db);
        ds.close();
        TestUtils.deleteTempTestingDir(factoryPath);

        db = null;
        im = null;
        ds = null;
        factory = null;
        factoryPath = null;
    }

}
