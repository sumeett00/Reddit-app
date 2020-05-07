package com.upgrad.reddit.api.controller;

import com.upgrad.reddit.api.model.*;
import com.upgrad.reddit.service.business.CommentBusinessService;
import com.upgrad.reddit.service.entity.CommentEntity;
import com.upgrad.reddit.service.entity.PostEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.CommentNotFoundException;
import com.upgrad.reddit.service.exception.InvalidPostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/")
public class CommentController {


    @Autowired
    private CommentBusinessService commentBusinessService;

    /**
     * A controller method to post an comment to a specific post.
     *
     * @param commentRequest - This argument contains all the attributes required to store comment details in the database.
     * @param postId    - The uuid of the post whose comment is to be posted in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CommentResponse> type object along with Http status CREATED.
     * @throws AuthorizationFailedException
     * @throws InvalidPostException
     */

    /**
     * A controller method to edit an comment in the database.
     *
     * @param commentEditRequest - This argument contains all the attributes required to store edited comment details in the database.
     * @param commentId          - The uuid of the comment to be edited in the database.
     * @param authorization     - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CommentEditResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws CommentNotFoundException
     */

    /**
     * A controller method to delete an comment in the database.
     *
     * @param commentId      - The uuid of the comment to be deleted in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CommentDeleteResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws CommentNotFoundException
     */

    /**
     * A controller method to fetch all the comments for a specific post in the database.
     *
     * @param postId    - The uuid of the post whose comments are to be fetched from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<List<CommentDetailsResponse>> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws InvalidPostException
     */

    //comment on the particular post
    @RequestMapping(method = RequestMethod.POST, path = "/post/{postId}/comment/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CommentResponse> createComment (@PathVariable("postId") final String postId, @RequestHeader("authorization") final String authorization, final CommentRequest commentRequest) throws AuthorizationFailedException {

        final ZonedDateTime now = ZonedDateTime.now();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUuid(UUID.randomUUID().toString());
        commentEntity.setComment(commentRequest.getComment());
        commentEntity.setDate(now);

        final CommentEntity createdComment = commentBusinessService.createComment(commentEntity , postId, authorization);
        CommentResponse commentResponse = new CommentResponse().id(createdComment.getUuid()).status("COMMENT CREATED");

        return new ResponseEntity<CommentResponse>(commentResponse, HttpStatus.CREATED);
    }

    //edit a comment in database
    @RequestMapping(method = RequestMethod.PUT, path = "/comment/edit/{commentId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CommentResponse> editCommentContent  (@PathVariable("commentId") final String commentId, @RequestHeader("authorization") final String authorization, final CommentEditRequest commentEditRequest) throws AuthorizationFailedException, CommentNotFoundException {

        final ZonedDateTime now = ZonedDateTime.now();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setComment(commentEditRequest.getContent());
        commentEntity.setDate(now);

        final CommentEntity editedComment = commentBusinessService.editCommentContent(commentEntity , commentId, authorization);
        CommentResponse commentResponse = new CommentResponse().id(editedComment.getUuid()).status("COMMENT EDITED");

        return new ResponseEntity<CommentResponse>(commentResponse, HttpStatus.OK);
    }

    //delete a comment in database
    @RequestMapping(method = RequestMethod.DELETE, path = "/comment/delete/{commentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CommentResponse> deleteComment  (@PathVariable("commentId") final String commentId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, CommentNotFoundException {
        final CommentEntity deletedComment = commentBusinessService.deleteComment(commentId, authorization);
        CommentResponse commentResponse = new CommentResponse().id(deletedComment.getUuid()).status("COMMENT DELETED!");

        return new ResponseEntity<CommentResponse>(commentResponse, HttpStatus.OK);
    }

    //fetch all the comments of a particular user from database
    @RequestMapping(method = RequestMethod.GET, path = "comment/all/{postId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CommentDetailsResponse>> getAllCommentsToPost  (@PathVariable("postId") final String postId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidPostException {

        final List<CommentEntity> allComments = commentBusinessService.getAllCommentsToPost(postId, authorization);
        final List<CommentDetailsResponse> allCommentDetailsResponse = new ArrayList<CommentDetailsResponse>();
        for(CommentEntity commentEntity : allComments) {
            CommentDetailsResponse commentDetailsResponse = new CommentDetailsResponse().id(commentEntity.getUuid()).postContent(commentEntity.getPost().getContent()).commentContent(commentEntity.getComment());
            allCommentDetailsResponse.add(commentDetailsResponse);
        }
        return new ResponseEntity<List<CommentDetailsResponse>>(allCommentDetailsResponse, HttpStatus.OK);
    }
}
