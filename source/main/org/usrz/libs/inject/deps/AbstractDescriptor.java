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
package org.usrz.libs.inject.deps;

import static org.usrz.libs.inject.utils.Parameters.notNull;

import org.usrz.libs.inject.TypeLiteral;

/**
 * A basic, abstract implementation of the {@link Descriptor} interface.
 *
 * @author <a href="mailto:pier@usrz.com">Pier Fumagalli</a>
 */
public abstract class AbstractDescriptor<T> implements Descriptor<T> {

    /** The {@link TypeLiteral} associated with this instance. */
    protected final TypeLiteral<T> type;

    /**
     * Create a new {@link AbstractDescriptor} associated with the
     * specified {@link TypeLiteral}.
     */
    protected AbstractDescriptor(TypeLiteral<T> type) {
        this.type = notNull(type, "Null type literal");
    }

    @Override
    public final TypeLiteral<T> getTypeLiteral() {
        return type;
    }

}
