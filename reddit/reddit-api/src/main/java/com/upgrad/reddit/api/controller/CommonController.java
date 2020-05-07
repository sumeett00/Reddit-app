package com.upgrad.reddit.api.controller;

import com.upgrad.reddit.api.model.UserDetailsResponse;
import com.upgrad.reddit.service.business.AuthenticationService;
import com.upgrad.reddit.service.business.CommonBusinessService;
import com.upgrad.reddit.service.dao.UserDao;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * A controller method to fetch the details of other user.
     *
     * @param userId        - The uuid of the user whose details are to be fetched from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<UserDetailsResponse> type object along with Http status OK.
     * @throws UserNotFoundException
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userId,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = commonBusinessService.getUser(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber()).dob(userEntity.getDob()).aboutMe(userEntity.getAboutMe())
                .country(userEntity.getCountry()).userName(userEntity.getUsername());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}
