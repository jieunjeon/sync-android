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

import com.cloudant.sync.datastore.DatastoreImpl;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.QueryableDatastore;
import com.cloudant.sync.sqlite.SQLDatabase;
import com.cloudant.sync.sqlite.SQLDatabaseQueue;
import com.cloudant.sync.util.SQLDatabaseTestUtils;
import com.cloudant.sync.util.TestUtils;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractIndexTestBase {

    String factoryPath = null;
    DatastoreManager factory = null;
    ForwardingDatastore fd = null;
    QueryableDatastore ds = null;
    SQLDatabaseQueue dbq = null;

    @Before
    public void setUp() throws Exception {
        factoryPath = TestUtils.createTempTestingDir(AbstractIndexTestBase.class.getName());
        assertThat(factoryPath, is(notNullValue()));
        factory = DatastoreManager.getInstance(factoryPath);
        assertThat(factory, is(notNullValue()));
        ds = (QueryableDatastore) factory.openDatastore(AbstractIndexTestBase.class.getSimpleName());
        assertThat(ds, is(notNullValue()));
        fd = new ForwardingDatastore(ds);
        dbq = ds.getQueryQueue();
        assertThat(fd, is(notNullValue()));
//        db = TestUtils.getDatabaseConnectionToExistingDb(im.getDatabase());
//        assertThat(db, is(notNullValue()));
//        assertThat(im.getQueue(), is(notNullValue()));
        String[] metadataTableList = new String[] { QueryConstants.INDEX_METADATA_TABLE_NAME };
//        SQLDatabaseTestUtils.assertTablesExist(TestUtils.getDatabaseConnectionToExistingDb(db),
//                                               metadataTableList);
    }

    @After
    public void tearDown() {
        ds.close();
//        assertThat(im.getQueue().isShutdown(), is(true));
//        TestUtils.deleteDatabaseQuietly(db); // FIXME make this work
        TestUtils.deleteTempTestingDir(factoryPath);

        dbq = null;
        ds = null;
        factory = null;
        factoryPath = null;
    }

}
