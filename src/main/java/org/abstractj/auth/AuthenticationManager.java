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

package org.abstractj.auth;

import org.abstractj.authz.IdentityManagement;
import org.abstractj.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha512Hash;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.Serializable;

@ApplicationScoped
public class AuthenticationManager {

    @Produces
    private Serializable sessionId;

    @Inject
    private IdentityManagement identityManagement;

    /**
     * Please, make sure to derive the passsword with Sha512Hash
     * @see https://shiro.apache.org/static/1.2.2/apidocs/org/apache/shiro/crypto/hash/Sha512Hash.html#Sha512Hash(java.lang.Object,%20java.lang.Object,%20int)
     * @param user
     */
    public boolean login(User user) {

        UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(),
                new Sha512Hash(user.getPassword()).toHex());

        SecurityUtils.getSubject().login(token);
        if (SecurityUtils.getSubject().isAuthenticated()) {
            sessionId = SecurityUtils.getSubject().getSession(true).getId();
        } else {
            throw new RuntimeException("Authentication failed");
        }

        return true;
    }

    public void logout() {
        SecurityUtils.getSubject().logout();
    }
}
