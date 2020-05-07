package com.upgrad.reddit.api.controller;

import com.upgrad.reddit.api.model.SigninResponse;
import com.upgrad.reddit.api.model.SignoutResponse;
import com.upgrad.reddit.api.model.SignupUserRequest;
import com.upgrad.reddit.api.model.SignupUserResponse;
import com.upgrad.reddit.service.business.AuthenticationService;
import com.upgrad.reddit.service.business.SignoutBusinessService;
import com.upgrad.reddit.service.business.SignupBusinessService;
import com.upgrad.reddit.service.business.UserBusinessService;
import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.AuthenticationFailedException;
import com.upgrad.reddit.service.exception.SignOutRestrictedException;
import com.upgrad.reddit.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

//@RequestMapping("/user")
//public class UserController {
//
//    @Autowired
//    private UserBusinessService userBusinessService;
//
//    /**
//     * A controller method for user signup.
//     *
//     * @param signupUserRequest - This argument contains all the attributes required to store user details in the database.
//     * @return - ResponseEntity<SignupUserResponse> type object along with Http status CREATED.
//     * @throws SignUpRestrictedException
//     */
//
//    /**
//     * A controller method for user authentication.
//     *
//     * @param authorization - A field in the request header which contains the user credentials as Basic authentication.
//     * @return - ResponseEntity<SigninResponse> type object along with Http status OK.
//     * @throws AuthenticationFailedException
//     */
//
//    /**
//     * A controller method for user signout.
//     *
//     * @param authorization - A field in the request header which contains the JWT token.
//     * @return - ResponseEntity<SignoutResponse> type object along with Http status OK.
//     * @throws SignOutRestrictedException
//     */
//
//}


@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED!!");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic: ")[1]);
        String decoded = new String(decode);
        String[] decodeArray = decoded.split(":");

        UserAuthEntity userAuthToken = authenticationService.authenticate(decodeArray[0], decodeArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserAuthEntity userToken = signoutBusinessService.signout(authorization);
        UserEntity userEntity = userToken.getUser();

        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }

}
