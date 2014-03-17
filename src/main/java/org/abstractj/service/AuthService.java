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

package org.abstractj.service;


import org.abstractj.auth.AuthenticationManager;
import org.abstractj.authz.IdentityManagement;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/auth")
public class AuthService {

    public static final String DEFAULT_ROLE = "simple";

    @Inject
    private IdentityManagement configuration;

    @Inject
    private AuthenticationManager authenticationManager;


    @POST
    @Path("/enroll")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(String loginName, String password) {

        try {
            configuration.create(loginName, password);
            configuration.grant(DEFAULT_ROLE).to(loginName);
            if (authenticationManager.login(loginName, password)) {
                return Response.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String loginName, String password) {
        try {
            if (authenticationManager.login(loginName, password)) {
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/logout")
    public void logout() {
        authenticationManager.logout();
    }

}
