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

import java.util.Date;

public class Response<T> extends AbstractPartialResponse<T> {

    private final PartialResponse<T> partial;
    private final T content;

    public Response(PartialResponse<T> partial, T content) {
        super(partial);
        this.partial = partial;
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String getLocation() {
        return partial.getLocation();
    }

    @Override
    public String getVectorClock() {
        return partial.getVectorClock();
    }

    @Override
    public Date getLastModified() {
        return partial.getLastModified();
    }

    @Override
    public Key getKey() {
        return partial.getKey();
    }

}
