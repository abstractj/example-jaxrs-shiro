# Apache Shiro PoC

This is just a project to demonstrate the integration with [AeroGear Android](https://github.com/aerogear/aerogear-android-cookbook). Not something production ready, use at your own risk, improve, change or send pull-requests.

## Enroll

- /rest/auth/enroll

      curl -3 -v -b cookies.txt -c cookies.txt -H "Accept: application/json" -H "Content-type: application/json" -d '{"loginName":"john","password":"123"}' -X POST http://localhost:8080/example-jaxrs-shiro/rest/auth/enroll

## Login

- /rest/auth/login

      curl -3 -v -b cookies.txt -c cookies.txt -H "Accept: application/json" -H "Content-type: application/json" -d '{"loginName":"john","password":"123"}' -X POST http://localhost:8080/example-jaxrs-shiro/rest/auth/login

## Authorization

The endpoint authorization happens with the usage of CDI interceptors, for this reason an annotation *@Secure* was created. For demonstration purposes, by default any user will have the role *simple*. 

- Endpoint protection:

      @Path("/grocery")
      public class GroceryService {

          @GET
          @Path("/bacons")
          @Produces(MediaType.APPLICATION_JSON)
          @Secure("simple")
          public List<String> bacons() {
                return Arrays.asList(new String[]{"Turkey", "Jowl", "Canadian", "Speck", "Pancetta"});
          }

          @GET
          @Path("/beers")
          @Produces(MediaType.APPLICATION_JSON)
          @Secure("admin")
          public List<String> beers() {
                return Arrays.asList(new String[]{"Belgium", "California", "Michigan", "Ireland", "British"});
          }
      }

### Now test it:

- **HTTP 200**: Authorized

      curl -b --cookie -v -X GET http://localhost:8080/example-jaxrs-shiro/rest/grocery/bacons

- **HTTP 401**: Unauthorized

      curl -b --cookie -v -X GET http://localhost:8080/example-jaxrs-shiro/rest/grocery/beers

### OpenShift URL

- http://shiro-abstractj.rhcloud.com/example-jaxrs-shiro/rest/auth/

