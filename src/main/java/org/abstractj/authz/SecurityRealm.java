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
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.ejb.Stateless;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Shiro realm configuration
 */
@Stateless
public class SecurityRealm extends AuthorizingRealm {

    private final IdentityManagement identityManagement;

    public SecurityRealm(){
        setName("SecurityRealm");
        setCredentialsMatcher(new HashedCredentialsMatcher(Sha512Hash.ALGORITHM_NAME));
        identityManagement = getIdentityManagement();
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authToken;

        User user = identityManagement.findByUsername(token.getUsername());

        if (user != null) {
            return new SimpleAuthenticationInfo(user.getId(), new Sha512Hash(user.getPassword()), getName());
            //TODO new Sha512Hash(source, salt, iterations);
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        try {

            Long userId = (Long) (principals.fromRealm(getName()).iterator().next());
            User user = identityManagement.findById(userId);
            if (user != null) {
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                for (Role role : user.getRoles()) {
                    info.addRole(role.getName());
                    info.addStringPermissions(role.getPermissions());
                }
                return info;
            } else {
                throw new RuntimeException("Not authorized");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Authorization has failed");
        }
    }

    private IdentityManagement getIdentityManagement(){

        final String JNDI_BEAN_MANAGER = "java:comp/BeanManager";
        IdentityManagement imgmt = null;
        try {
            BeanManager beanManager = InitialContext.doLookup(JNDI_BEAN_MANAGER);
            Bean identityManagement = (Bean)beanManager.getBeans(IdentityManagement.class).iterator().next();
            CreationalContext context = beanManager.createCreationalContext(identityManagement);
            imgmt = (IdentityManagement)beanManager.getReference(identityManagement,
                    IdentityManagement.class, context);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return imgmt;
    }

}
