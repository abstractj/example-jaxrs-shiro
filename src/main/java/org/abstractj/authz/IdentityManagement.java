/**
 * Copyright 2013 Bruno Oliveira, and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.abstractj.authz;

import org.abstractj.model.User;

import java.util.Set;

public interface IdentityManagement {

    GrantConfiguration grant(String... roles);

    User findByUsername(String username) throws RuntimeException;

    User findById(long id) throws RuntimeException;

    void remove(String username);

    void create(User user);

    static interface GrantMethods<User> {

        void to(String username);

        GrantMethods<User> roles(String[] roles);

    }

    boolean hasRoles(Set<String> roles);
}
