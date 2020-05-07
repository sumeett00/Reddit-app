package com.upgrad.reddit.service.business;


import com.upgrad.reddit.service.dao.CommonDao;
import com.upgrad.reddit.service.dao.UserDao;
import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommonDao commonDao;

    /**
     * The method implements the business logic for userProfile endpoint.
     */
    public UserEntity getUser(String uuid, String authorization) throws UserNotFoundException, AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        UserEntity userEntity = userDao.getUserByUsername(uuid);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose post details are to be seen does not exist");
        }
        return userEntity;
    }

}
