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
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.subject.Subject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Set;

@ApplicationScoped
public class IdentityManagement {

    @Inject
    private EntityManager entityManager;

    @Inject
    private GrantConfiguration grantConfiguration;

    @Inject
    private Subject subject;

    public GrantConfiguration grant(String... roles) {
        return grantConfiguration.roles(roles);
    }

    public User findByUsername(String username) throws RuntimeException {
        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username)
                .getSingleResult();
        if (user == null) {
            throw new RuntimeException("AeroGearUser do not exist");
        }
        return user;
    }

    public User findById(long id) throws RuntimeException {
        return entityManager.find(User.class, id);
    }

    public void remove(String username) {
        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username)
                .getSingleResult();
        if (user == null) {
            throw new RuntimeException("AeroGearUser do not exist");
        }
        entityManager.remove(user);
    }

    public void create(String username, String password) {
        User newUser = new User(username,
                new Sha512Hash(password).toHex());
        entityManager.persist(newUser);
    }

    public boolean hasRoles(Set<String> roles) {
        return subject.hasAllRoles(roles);
    }
}
