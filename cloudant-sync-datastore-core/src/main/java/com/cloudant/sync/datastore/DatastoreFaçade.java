/*
 *   Copyright © 2016 IBM Corporation. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the
 *   License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *   either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.cloudant.sync.datastore;

import com.cloudant.sync.datastore.encryption.KeyProvider;
import com.cloudant.sync.query.IndexManager;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * Created by rhys on 13/07/2016.
 */
public class DatastoreFaçade implements InvocationHandler {


    private final DatastoreImpl datastore;
    private final IndexManager indexManager;
    private final Class<? extends DatastoreImpl> datastoreClazz;
    private final Class<? extends IndexManager> indexManagerClazz;

    public DatastoreFaçade(String dir, String name, KeyProvider keyProvider) throws DatastoreException, SQLException, IOException {
        datastore = new DatastoreImpl(dir, name, keyProvider);
        datastoreClazz = datastore.getClass();
        indexManager = new IndexManager(datastore);
        indexManagerClazz = indexManager.getClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals("close")){
            indexManager.close();
            datastore.close();
            return null;
        }

        try {
            Method targetMethod = datastoreClazz.getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(datastore, args);
        } catch (NoSuchMethodException e){
            Method targetMethod = indexManagerClazz.getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(indexManager, args);
        }
    }

    public DatastoreImpl getDatastoreImplementation(){
        return datastore;
    }
}
