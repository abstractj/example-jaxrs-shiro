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

import org.abstractj.model.Role;
import org.abstractj.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class GrantConfiguration implements IdentityManagement.GrantMethods<User> {

    @Inject
    private EntityManager entityManager;

    private Set<Role> list;

    public GrantConfiguration roles(String[] roles) {
        list = new HashSet<Role>();
        for (String name : roles) {
            Role role = entityManager.createNamedQuery("Role.findByName", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
            list.add(role);
        }
        return this;
    }

    public void to(String username) {

        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("loginName", username)
                .getSingleResult();

        user.setRoles(list);
        entityManager.merge(user);

    }
}
