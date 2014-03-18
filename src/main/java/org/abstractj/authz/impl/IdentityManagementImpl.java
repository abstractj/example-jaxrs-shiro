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

package org.abstractj.authz.impl;

import org.abstractj.authz.GrantConfiguration;
import org.abstractj.authz.IdentityManagement;
import org.abstractj.model.User;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.subject.Subject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class IdentityManagementImpl implements IdentityManagement{

    @Inject
    private EntityManager entityManager;

    @Inject
    private GrantConfiguration grantConfiguration;

    private static final Logger LOGGER = Logger.getLogger(IdentityManagementImpl.class.getSimpleName());

    @Inject
    private Subject subject;

    @Override
    public GrantConfiguration grant(String... roles) {
        return grantConfiguration.roles(roles);
    }

    @Override
    public User findByUsername(String username) throws RuntimeException {
        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("loginName", username)
                .getSingleResult();
        if (user == null) {
            throw new RuntimeException("AeroGearUser do not exist");
        }
        return user;
    }

    @Override
    public User findById(long id) throws RuntimeException {
        return entityManager.find(User.class, id);
    }

    @Override
    public void remove(String username) {
        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("loginName", username)
                .getSingleResult();
        if (user == null) {
            throw new RuntimeException("AeroGearUser do not exist");
        }
        entityManager.remove(user);
    }

    /**
     * Please, make sure to derive the passsword with Sha512Hash
     * @see https://shiro.apache.org/static/1.2.2/apidocs/org/apache/shiro/crypto/hash/Sha512Hash.html#Sha512Hash(java.lang.Object,%20java.lang.Object,%20int)
     * @param user
     */
    @Override
    public void create(User user) {
        User newUser = new User(user.getLoginName(),
                new Sha512Hash(user.getPassword()).toHex());
        entityManager.persist(newUser);

    }

    @Override
    public boolean hasRoles(Set<String> roles) {
        LOGGER.info("all: " + roles);
        return subject.hasAllRoles(roles);
    }
}
