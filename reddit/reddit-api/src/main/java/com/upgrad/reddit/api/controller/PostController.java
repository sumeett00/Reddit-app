package com.upgrad.reddit.api.controller;

import com.upgrad.reddit.api.model.*;
import com.upgrad.reddit.service.business.AuthenticationService;
import com.upgrad.reddit.service.business.PostBusinessService;
import com.upgrad.reddit.service.entity.PostEntity;
import com.upgrad.reddit.service.exception.AuthorizationFailedException;
import com.upgrad.reddit.service.exception.InvalidPostException;
import com.upgrad.reddit.service.exception.SignOutRestrictedException;
import com.upgrad.reddit.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostBusinessService postBusinessService;

    /**
     * A controller method to create a post.
     *
     * @param postRequest - This argument contains all the attributes required to store post details in the database.
     * @param authorization   - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<PostResponse> type object along with Http status CREATED.
     * @throws AuthorizationFailedException
     */

    /**
     * A controller method to fetch all the posts from the database.
     *
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<List<PostDetailsResponse>> type object along with Http status OK.
     * @throws AuthorizationFailedException
     */

    /**
     * A controller method to edit the post in the database.
     *
     * @param postEditRequest - This argument contains all the attributes required to edit the post details in the database.
     * @param postId          - The uuid of the post to be edited in the database.
     * @param authorization       - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<PostEditResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws InvalidPostException
     */

    /**
     * A controller method to delete the post in the database.
     *
     * @param postId    - The uuid of the post to be deleted in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<PostDeleteResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws InvalidPostException
     */

    /**
     * A controller method to fetch all the posts posted by a specific user.
     *
     * @param userId        - The uuid of the user whose posts are to be fetched from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<List<PostDetailsResponse>> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
//Create a post
  @Autowired
    private AuthenticationService userAuthBusinessService;

  @RequestMapping(method = RequestMethod.POST, path = "/post/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<PostResponse> createPost(@RequestHeader("authorization") final String authorization,final PostRequest postRequest ) throws AuthorizationFailedException, SignOutRestrictedException{
      final ZonedDateTime now = ZonedDateTime.now();
      PostEntity postEntity = new PostEntity();
      postEntity.setUuid(UUID.randomUUID().toString());
      postEntity.setContent(postRequest.getContent());
      postEntity.setDate(now);

      final PostEntity createdPost = postBusinessService.createPost(postEntity , authorization);
      PostResponse postResponse = new PostResponse().id(createdPost.getUuid()).status("POST CREATED");

      return new ResponseEntity<PostResponse>(postResponse, HttpStatus.CREATED);
  }

//to fetch all the post from the database

    @RequestMapping(method = RequestMethod.GET, path = "/post/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PostDetailsResponse>> GetAllPosts(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, SignOutRestrictedException {
       final List<PostEntity> allPosts= (List<PostEntity>) postBusinessService.getPosts(authorization);
        List<PostDetailsResponse> postResponse = postslist(allPosts);

        return new ResponseEntity<List<PostDetailsResponse>>(postResponse, HttpStatus.OK);
}
//post list
    private List<PostDetailsResponse> postslist(List<PostEntity> allPosts) {
        List<PostDetailsResponse> listofposts = new ArrayList<>();
        for ( PostEntity postEntity : allPosts){
            PostDetailsResponse Response = new PostDetailsResponse();
            Response.id(postEntity.getUuid());
            Response.content(postEntity.getContent());
            listofposts.add(Response);
        }
        return listofposts;
  }

 //edit post in the database
 @RequestMapping(method = RequestMethod.PUT , path = "/post/edit/{postId}" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
 public ResponseEntity<PostEditResponse> editPostContent(@PathVariable("postId") final String postId , @RequestHeader("authorization") final String authorization, PostEditRequest postEditRequest)
         throws AuthorizationFailedException, InvalidPostException, SignOutRestrictedException {

     PostEntity postEntity = new PostEntity();
     postEntity.setContent(postEditRequest.getContent());
     postEntity.setDate(ZonedDateTime.now());
     PostEntity editedPost = postBusinessService.editPostContent(postEntity, authorization, postId);
     PostEditResponse  postEditResponse = new PostEditResponse().id(editedPost.getUuid()).status("POST EDITED");

     return new ResponseEntity<PostEditResponse>(postEditResponse,HttpStatus.OK);
 }
 //delete a post
 @RequestMapping(method=RequestMethod.DELETE,path="/post/delete/{postId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
 public ResponseEntity<PostDeleteResponse>DeletePost(@RequestHeader("authorization") final String authorization, @PathVariable("postId") final String postid) throws AuthorizationFailedException, InvalidPostException, SignOutRestrictedException {

     PostEntity deletedPost = postBusinessService.deletePost(postid, authorization);
     PostDeleteResponse postDeleteResponse = new PostDeleteResponse().id(deletedPost.getUuid()).status("POST DELETED");



     return new ResponseEntity<PostDeleteResponse>(postDeleteResponse, HttpStatus.OK);
 }

 //list of post by particular user
 @RequestMapping(method = RequestMethod.GET , path = "/post/all/{userId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
 public ResponseEntity<List<PostDetailsResponse>> getAllPostsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization )
         throws AuthorizationFailedException , UserNotFoundException, SignOutRestrictedException {

     final List<PostEntity> postEntityList = postBusinessService. getAllPostsByUser(userId, authorization);
     final List<PostDetailsResponse> allPostDetailsResponse = new ArrayList<PostDetailsResponse>();
     for(PostEntity postEntity : postEntityList) {
         PostDetailsResponse postDetailsResponse = new PostDetailsResponse().id(postEntity.getUuid()).content(postEntity.getContent());
         allPostDetailsResponse.add(postDetailsResponse);
     }
     return new ResponseEntity<List<PostDetailsResponse>>(allPostDetailsResponse, HttpStatus.OK);
 }

}
