package com.upgrad.reddit.service.business;

import com.upgrad.reddit.service.dao.CommentDao;
import com.upgrad.reddit.service.dao.PostDao;
import com.upgrad.reddit.service.dao.UserDao;
import com.upgrad.reddit.service.entity.CommentEntity;
import com.upgrad.reddit.service.entity.PostEntity;
import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.CommentNotFoundException;
import com.upgrad.reddit.service.exception.InvalidPostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class CommentBusinessService {


    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private PostDao postDao;


    /**
     * The method implements the business logic for createComment endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentEntity createComment(CommentEntity commentEntity, String postId, String authorization) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an comment");
        }
        commentEntity.setComment(userAuthEntity.getUuid());
        return commentDao.createComment(commentEntity);

    }

    public PostEntity getPostByUuid(String Uuid) throws InvalidPostException {

        PostEntity postEntity = commentDao.getPostByUuid(Uuid);
        if(postEntity==null)
        {
            throw new InvalidPostException("POS-001","The post with entered uuid whose details are to be seen does not exist");
        }
        return commentDao.getPostByUuid(Uuid);

    }


    /**
     * The method implements the business logic for editCommentContent endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentEntity editCommentContent(CommentEntity commentEntity, String commentId, String authorization) throws AuthorizationFailedException, CommentNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        CommentEntity commentEntity1=commentDao.getCommentByUuid(commentId);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the comment");
        }
        if (commentEntity1 == null) {
            throw new CommentNotFoundException("COM-001", "Entered comment uuid does not exist");
        }
        if (userAuthEntity.getUser().getId() != commentEntity1.getUser().getId()){
            throw new AuthorizationFailedException("ATHR-003", "Only the comment owner can edit the comment");
        }
        commentEntity.setId(commentEntity1.getId());
        commentEntity.setUuid(commentEntity1.getUuid());
        commentEntity.setUser(commentEntity1.getUser());
        return commentDao.editComment(commentEntity);
    }

    /**
     * The method implements the business logic for deleteComment endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentEntity deleteComment(String commentId, String authorization) throws AuthorizationFailedException, CommentNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        CommentEntity commentEntity = commentDao.getCommentByUuid(commentId);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a comment");
        }
        if (commentEntity == null)
        {
            throw new CommentNotFoundException("COM-001","Entered comment uuid does not exist");

        }
        if(!(userAuthEntity.getUser().getRole().equals("admin"))&&(userAuthEntity.getUser().getId()!=commentEntity.getUser().getId()))
        {
            throw new AuthorizationFailedException("ATHR-003","Only the comment owner or admin can delete the comment");
        }
        return commentDao.deleteComment(commentEntity);
    }

    /**
     * The method implements the business logic for getAllCommentsToPost endpoint.
     */
    public TypedQuery<CommentEntity> getCommentsByPost(String postId, String authorization) throws AuthorizationFailedException, InvalidPostException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccesstoken(authorization);
        PostEntity postEntity=getPostByUuid(postId);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the comments");
        }
        if(postEntity == null){
            throw new InvalidPostException("POS-001", "The post with entered uuid whose details are to be seen does not exist");
        }
        return commentDao.getCommentsByPost(postEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CommentEntity> getAllCommentsToPost(final String postId, final String authorizationToken) throws AuthorizationFailedException, InvalidPostException {
        PostEntity postEntity = postDao.getPostByUuid(postId);
        if (postEntity == null) {
            throw new InvalidPostException("POS-001", "The post with entered uuid whose details are to be seen does not exist");
        }
        UserAuthEntity userAuthTokenEntity = commentDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the comments");
        }
        List<CommentEntity> commentEntityList = commentDao.getAllComments(postEntity);

        return commentEntityList;
    }


}
