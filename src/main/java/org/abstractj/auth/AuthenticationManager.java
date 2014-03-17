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

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.subject.Subject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.Serializable;

@ApplicationScoped
public class AuthenticationManager {

    @Inject
    private Subject subject;

    @Produces
    private Serializable sessionId;

    public boolean login(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username,
                new Sha512Hash(password).toHex());

        subject.login(token);
        if (subject.isAuthenticated()) {
            sessionId = subject.getSession().getId();
        } else {
            throw new RuntimeException("Authentication failed");
        }

        return true;
    }

    public void logout() {
        subject.logout();
    }
}
