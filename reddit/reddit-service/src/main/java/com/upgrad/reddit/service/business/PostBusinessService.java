package com.upgrad.reddit.service.business;


import com.upgrad.reddit.service.dao.PostDao;
import com.upgrad.reddit.service.dao.UserDao;
import com.upgrad.reddit.service.entity.PostEntity;
import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.InvalidPostException;
import com.upgrad.reddit.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class PostBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;


    /**
     * The method implements the business logic for createPost endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public PostEntity createPost(PostEntity postEntity, String authorization) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        if(userAuthEntity==null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(userAuthEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a post");
        }
        postEntity.setUser(userAuthEntity.getUser());
        return postDao.createPost(postEntity);


    }

    /**
     * The method implements the business logic for getAllPosts endpoint.
     */
    public TypedQuery<PostEntity> getPosts(String authorization) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        if(userAuthEntity==null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        }
        if(userAuthEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a post");
        }
        return postDao.getPosts();
    }

    /**
     * The method implements the business logic for editPostContent endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public PostEntity editPostContent(PostEntity postEntity, String postId, String authorization) throws AuthorizationFailedException, InvalidPostException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        PostEntity postEntity1=postDao.getPostByUuid(postId);
        if(userAuthEntity==null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        }
        if(userAuthEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a post");
        }
        if(postEntity1==null)
        {
            throw new InvalidPostException("POS-001","Entered post uuid does not exist");

        }
        if(userAuthEntity.getUser().getId()!=postEntity1.getUser().getId())
        {
            throw new AuthorizationFailedException("ATHR-003","Only the post owner or admin can delete the post");
        }
        postEntity.setId(postEntity1.getId());
        postEntity.setUuid(postEntity1.getUuid());
        postEntity.setUser(postEntity1.getUser());
        return postDao.editPost(postEntity);
    }

    /**
     * The method implements the business logic for deletePost endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public PostEntity deletePost(String postId, String authorization) throws AuthorizationFailedException, InvalidPostException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        PostEntity postEntity=postDao.getPostByUuid(postId);
        if(userAuthEntity==null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        }
        if(userAuthEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a post");
        }
        if(postEntity==null)
        {
            throw new InvalidPostException("POS-001","Entered post uuid does not exist");

        }
        if(!(userAuthEntity.getUser().getRole().equals("admin"))&&(userAuthEntity.getUser().getId() != postEntity.getUser().getId())){
            throw new AuthorizationFailedException("ATHR-003", "Only the post owner or admin can delete the post");
        }
        return postDao.deletePost(postEntity);
    }

    /**
     * The method implements the business logic for getAllPostsByUser endpoint.
     */
    public TypedQuery<PostEntity> getPostsByUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        UserEntity userEntity=userDao.getUserByUsername(userId);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all posts posted by a specific user");
        }
        if(userEntity == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose post details are to be seen does not exist");
        }
        return postDao.getPostsByUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<PostEntity> getAllPostsByUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        UserEntity userEntity = userDao.getUser(userId);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all posts posted by a specific user");
        }
        if(userEntity == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose post details are to be seen does not exist");
        }
        return postDao.getAllPostsByUser(userEntity);
    }
}
