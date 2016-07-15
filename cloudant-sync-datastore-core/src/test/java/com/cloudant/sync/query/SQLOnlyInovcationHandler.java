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

package com.cloudant.sync.query;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreFaçade;
import com.cloudant.sync.datastore.DatastoreImpl;
import com.cloudant.sync.datastore.Query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * Created by rhys on 14/07/2016.
 */
public class SQLOnlyInovcationHandler implements InvocationHandler {

    private final Datastore datastore;
    private final Class<? extends Datastore> datastoreClazz;

    public SQLOnlyInovcationHandler(Datastore ds){
        datastore = ds;
        datastoreClazz = ds.getClass();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        if(method.getName().equals("find")){

            // extract the arguments.
            Map<String, Object> selector = (Map<String, Object>) args[0];
            Long skip = 0L;
            Long limit = 0L;
            List<String> fields = null;
            List<Map<String, String>> sortDocument = null;

            switch (args.length) {
                case 5:
                    sortDocument = (List<Map<String, String>>) args[4];
                case 4:
                    fields = (List<String>) args[3];
                case 3:
                    limit = (Long) args[2];
                case 2:
                    skip = (Long) args[1];
            }

            if (selector == null) {
                return null;
            }

            if (!datastore.updateAllIndexes()) {
                return null;
            }

            Query query = ((DatastoreFaçade) Proxy.getInvocationHandler(datastore)).getQueryImplementation();
            DatastoreImpl dsImpl = ((DatastoreFaçade) Proxy.getInvocationHandler(datastore)).getDatastoreImplementation();

            MockSQLOnlyQueryExecutor queryExecutor = new MockSQLOnlyQueryExecutor(query.getDatabase(),
                    dsImpl,
                    query.getQueue());
            Map<String, Object> indexes = datastore.listIndexes();
            return queryExecutor.find(selector, indexes, skip, limit, fields, sortDocument);



        } else {
            Method target = datastoreClazz.getMethod(method.getName(), method.getParameterTypes());
            return target.invoke(datastore, args);
        }
    }
}
