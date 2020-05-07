package com.upgrad.reddit.service.business;

import com.upgrad.reddit.service.dao.UserDao;
import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutBusinessService {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signout(String authorization) throws SignOutRestrictedException {
        UserAuthEntity userAuthToken = authenticationService.getUserAuthToken(authorization);
        UserEntity user = userAuthToken.getUser();
        if(userAuthToken==null)
        {
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        userAuthToken.setLogoutAt(ZonedDateTime.now());
        userDao.updateAuthToken(userAuthToken);
        return userAuthToken;
    }
}
