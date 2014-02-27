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
package org.usrz.libs.riak;

import java.io.IOException;
import java.util.concurrent.Future;

import org.usrz.libs.riak.request.BucketedRequest;
import org.usrz.libs.riak.request.KeyedRequest;
import org.usrz.libs.riak.request.OptionalBodyRequest;

public abstract class AbstractContentRequest<T, R extends Request<T>
                                                & KeyedRequest<T, R>
                                                & BucketedRequest<T, R>
                                                & OptionalBodyRequest<T, R>>
extends AbstractRequest<T, R>
implements OptionalBodyRequest<T, R> {

    protected final ContentHandler<T> handler;
    private boolean returnBody = true;

    protected AbstractContentRequest(Key key, ContentHandler<T> handler) {
        super(key);
        if (handler == null) throw new NullPointerException("Null response handler");
        this.handler = handler;
    }

    protected AbstractContentRequest(Bucket bucket, ContentHandler<T> handler) {
        super(bucket);
        if (handler == null) throw new NullPointerException("Null response handler");
        this.handler = handler;
    }

    protected AbstractContentRequest(Bucket bucket, String key, ContentHandler<T> handler) {
        super(bucket, key);
        if (handler == null) throw new NullPointerException("Null response handler");
        this.handler = handler;
    }

    /* ====================================================================== */

    @Override
    public final R setReturnBody(boolean returnBody) {
        this.returnBody = returnBody;
        return thisInstance;
    }

    public final boolean getReturnBody() {
        return this.returnBody;
    }

    /* ====================================================================== */

    @Override
    protected final Future<Response<T>> execute(Bucket bucket)
    throws IOException {
        return this.execute(bucket, handler);
    }

    protected abstract Future<Response<T>> execute(Bucket bucket, ContentHandler<T> handler)
    throws IOException;

    @Override
    protected final Future<Response<T>> execute(Key key)
    throws IOException {
        return this.execute(key, handler);
    }

    protected abstract Future<Response<T>> execute(Key key, ContentHandler<T> handler)
    throws IOException;

}
