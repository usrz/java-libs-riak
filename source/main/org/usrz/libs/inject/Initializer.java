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
package org.usrz.libs.inject;

import javax.inject.Singleton;

public interface Initializer {

    /**
     * Request that this binding is <em>eagerly injected</em> by the
     * {@link Injector} upon its creation.
     */
    public void withEagerInjection();

    /**
     * Consider the instance associated with this binding as a
     * {@link Singleton}.
     */
    public void asSingleton();

    /**
     * Consider the instance associated with this binding as a
     * {@linkplain #withEagerInjection() eagerly injected} {@link Singleton}.
     */
    default void asEagerSingleton() {
        withEagerInjection();
        asSingleton();
    }

}
