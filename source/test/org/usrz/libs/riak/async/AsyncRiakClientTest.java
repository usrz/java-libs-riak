/* ========================================================================== *
 * Copyright 2014 USRZ.com and Pier Paolo Fumagalli                           *
 * -------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *  http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * ========================================================================== */
package org.usrz.libs.riak.async;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;

import org.testng.annotations.Test;
import org.usrz.libs.logging.Log;
import org.usrz.libs.riak.Bucket;
import org.usrz.libs.riak.Key;
import org.usrz.libs.riak.Response;
import org.usrz.libs.riak.RiakClient;
import org.usrz.libs.riak.StoreRequest;
import org.usrz.libs.riak.annotations.RiakIndex;
import org.usrz.libs.riak.annotations.RiakLink;
import org.usrz.libs.riak.annotations.RiakMetadata;
import org.usrz.libs.testing.AbstractTest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ning.http.client.AsyncHttpClient;

public class AsyncRiakClientTest extends AbstractTest {

    private final Log log = new Log();

    @Test(groups="local")
    public void testGetBucketsAndKeys()
    throws Exception {
        final AsyncRiakClient client = new AsyncRiakClient(new AsyncHttpClient());

        int buckets = 0;
        int keys = 0;
        for (Bucket bucket: client.getBuckets()) {
            buckets ++;
            log.debug("Found bucket \"%s\"", bucket.getName());
            for (Key key: bucket.getKeys()) {
                keys ++;
                log.debug("   in bucket \"%s\" found key \"%s\"", key.getBucketName(), key.getName() );
            }
        }
        log.info("Found a total of %d buckets and %d keys", buckets, keys);
    }

    @Test(groups="local")
    public void testStoreFetchDelete()
    throws Exception {
        final RiakClient client = new AsyncRiakClient(new AsyncHttpClient());
        final Bucket bucket = client.getBucket("test");

        final TestObject object = new TestObject();

        /* Create a new object */
        object.setValue("foobar1");
        final Future<Response<TestObject>> storeFuture1 = bucket.store(object).execute();
        final Response<TestObject> storeResponse1 = storeFuture1.get();

        log.info("Store 1: status=%d success=%b entity=%s", storeResponse1.getStatus(), storeResponse1.isSuccessful(), storeResponse1.getContent());
        assertEquals(storeResponse1.getStatus(), 201);
        assertEquals(storeResponse1.isSuccessful(), true);
        assertEquals(storeResponse1.getContent(), object);
        assertNotSame(storeResponse1.getContent(), object);

        /* Modify what we've created */
        object.setValue("foobar2");
        final Future<Response<TestObject>> storeFuture2 = bucket.store(object, storeResponse1.getKey().getName()).execute();
        final Response<TestObject> storeResponse2 = storeFuture2.get();

        log.info("Store 2: status=%d success=%b entity=%s", storeResponse2.getStatus(), storeResponse2.isSuccessful(), storeResponse2.getContent());
        assertEquals(storeResponse2.getStatus(), 200);
        assertEquals(storeResponse2.isSuccessful(), true);
        assertEquals(storeResponse2.getContent(), object);
        assertNotSame(storeResponse2.getContent(), object);
        assertEquals(storeResponse2.getKey(), storeResponse1.getKey());

        /* Fetch what we've created */
        final Future<Response<TestObject>> fetchFuture1 = bucket.fetch(storeResponse2.getKey().getName(), TestObject.class).execute();
        final Response<TestObject> fetchResponse1 = fetchFuture1.get();

        log.info("Fetch 1: status=%d success=%b entity=%s", fetchResponse1.getStatus(), fetchResponse1.isSuccessful(), fetchResponse1.getContent());
        assertEquals(fetchResponse1.getStatus(), 200);
        assertEquals(fetchResponse1.isSuccessful(), true);
        assertEquals(fetchResponse1.getContent(), object);
        assertNotSame(fetchResponse1.getContent(), object);
        assertEquals(fetchResponse1.getKey(), storeResponse2.getKey());

        /* Fetch something that does NOT exist (no exceptions!) */
        final Future<Response<TestObject>> fetchFuture2 = bucket.fetch(fetchResponse1.getKey().getName() + "_foobar", TestObject.class).execute();
        final Response<TestObject> fetchResponse2 = fetchFuture2.get();

        log.info("Fetch 2: status=%d success=%b entity=%s", fetchResponse2.getStatus(), fetchResponse2.isSuccessful(), fetchResponse2.getContent());
        assertEquals(fetchResponse2.getStatus(), 404);
        assertEquals(fetchResponse2.isSuccessful(), false);
        assertNull(fetchResponse2.getContent());
        assertEquals(fetchResponse2.getKey().getName(), fetchResponse1.getKey().getName() + "_foobar");

        /* Delete what we've created */
        final Future<Response<Boolean>> deleteFuture1 = bucket.delete(fetchResponse1.getKey().getName()).execute();
        final Response<Boolean> deleteResponse1 = deleteFuture1.get();
        log.info("Delete 1: status=%d success=%b entity=%s", deleteResponse1.getStatus(), deleteResponse1.isSuccessful(), deleteResponse1.getContent());
        assertEquals(deleteResponse1.getStatus(), 204);
        assertEquals(deleteResponse1.isSuccessful(), true);
        assertNotNull(deleteResponse1.getContent());
        assertTrue(deleteResponse1.getContent());
        assertEquals(deleteResponse1.getKey(), fetchResponse1.getKey());

        /* Attempt to delete again, we should not fail */
        final Future<Response<Boolean>> deleteFuture2 = bucket.delete(deleteResponse1.getKey().getName()).execute();
        final Response<Boolean> deleteResponse2 = deleteFuture2.get();
        log.info("Delete 2: status=%d success=%b entity=%s", deleteResponse2.getStatus(), deleteResponse2.isSuccessful(), deleteResponse2.getContent());
        assertEquals(deleteResponse2.getStatus(), 404);
        assertEquals(deleteResponse2.isSuccessful(), false);
        assertNull(deleteResponse2.getContent());
        assertEquals(deleteResponse2.getKey(), deleteResponse1.getKey());
    }

    /* ====================================================================== */

    public static class TestObject {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            if (object == null) return false;
            try {
                final TestObject testObject = ((TestObject) object);
                return testObject.value == null ? value == null : testObject.value.equals(value);
            } catch (ClassCastException exception) {
                return false;
            }
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "[" + value + "]@" + Integer.toHexString(hashCode());
        }
    }

    /* ====================================================================== */

    @Test(groups="local")
    public void testAnnotations()
    throws Exception {
        final RiakClient client = new AsyncRiakClient(new AsyncHttpClient());
        final Bucket bucket = client.getBucket("test");

        final AnnotatedObject object = new AnnotatedObject("someValue",
                                                           new String[] { "theIndexValue1", "theIndexValue2" },
                                                           new String[] { "metadataValue1", "metadataValue2" },
                                                           new Key(client, "foo1", "bar1"),
                                                           new Key(client, "foo2", "bar2"));

        final StoreRequest<AnnotatedObject> storeRequest1 = bucket.store(object).setReturnBody(true);
        assertTrue(storeRequest1.getIndexMap().containsValue("myindex_bin", "theIndexValue1"));
        assertTrue(storeRequest1.getIndexMap().containsValue("myindex_bin", "theIndexValue2"));
        assertTrue(storeRequest1.getMetadata().containsValue("mymetadata",  "metadataValue1"));
        assertTrue(storeRequest1.getMetadata().containsValue("mymetadata",  "metadataValue2"));

        final Response<AnnotatedObject> storeResponse1 = storeRequest1.execute().get();
        assertTrue(storeResponse1.getIndexMap().containsValue("myindex_bin", "theIndexValue1"));
        assertTrue(storeResponse1.getIndexMap().containsValue("myindex_bin", "theIndexValue2"));
        assertTrue(storeResponse1.getMetadata().containsValue("mymetadata",  "metadataValue1"));
        assertTrue(storeResponse1.getMetadata().containsValue("mymetadata",  "metadataValue2"));


        final StoreRequest<AnnotatedObject> storeRequest2 = bucket.store(object).setReturnBody(false);
        assertTrue(storeRequest2.getIndexMap().containsValue("myindex_bin", "theIndexValue1"));
        assertTrue(storeRequest2.getIndexMap().containsValue("myindex_bin", "theIndexValue2"));
        assertTrue(storeRequest2.getMetadata().containsValue("mymetadata",  "metadataValue1"));
        assertTrue(storeRequest2.getMetadata().containsValue("mymetadata",  "metadataValue2"));

        final Response<AnnotatedObject> storeResponse2 = storeRequest2.execute().get();
        assertTrue(storeResponse2.getIndexMap().isEmpty());
        assertTrue(storeResponse2.getMetadata().isEmpty());


        client.delete(storeResponse1.getKey()).execute().get();
        client.delete(storeResponse2.getKey()).execute().get();

    }

    /* ====================================================================== */

    public static class AnnotatedObject extends TestObject {

        private Collection<String> myIndex;
        private Collection<String> myMetadata;
        private Key firstLink;
        private Key secondLink;

        public AnnotatedObject() {
            super();
        }

        private AnnotatedObject(String value, String[] index, String[] metadata,
                                Key firstLink, Key secondLink) {
            setValue(value);
            setMyIndex(Arrays.asList(index));
            setMyMetadata(Arrays.asList(metadata));
            setFirstLink(firstLink);
            setSecondLink(secondLink);
        }

        @RiakIndex
        public Collection<String> getMyIndex() {
            return myIndex;
        }

        @RiakIndex
        public void setMyIndex(Collection<String> myIndex) {
            this.myIndex = myIndex;
        }

        @RiakMetadata
        public Collection<String> getMyMetadata() {
            return myMetadata;
        }

        @RiakMetadata
        public void setMyMetadata(Collection<String> myMetadata) {
            this.myMetadata = myMetadata;
        }

        @RiakLink @JsonIgnore
        public Key getFirstLink() {
            return firstLink;
        }

        @RiakLink @JsonIgnore
        public void setFirstLink(Key firstLink) {
            this.firstLink = firstLink;
        }

        @RiakLink @JsonIgnore
        public Key getSecondLink() {
            return secondLink;
        }

        @RiakLink @JsonIgnore
        public void setSecondLink(Key secondLink) {
            this.secondLink = secondLink;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            if (object == null) return false;
            if (super.equals(object)) try {
                final AnnotatedObject annotated = ((AnnotatedObject) object);
                return annotated.myIndex == null ? myIndex == null : annotated.myIndex.equals(myIndex);
            } catch (ClassCastException exception) {
                /* Just return false below */
            }
            return false;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "[val=" + getValue() + ",idx=" + myIndex + "]@" + Integer.toHexString(hashCode());
        }
    }
}
