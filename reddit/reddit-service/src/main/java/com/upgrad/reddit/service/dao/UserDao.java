package com.upgrad.reddit.service.dao;

import com.upgrad.reddit.service.entity.UserAuthEntity;
import com.upgrad.reddit.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * UserDao class provides the database access for all the endpoints in user controller.
 */
@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByUsername(String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createUserAuth(UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }


    public UserAuthEntity getUserAuthByAccesstoken(String accesstoken) {
        try {
            return entityManager.createNamedQuery("userAuthByAccesstoken", UserAuthEntity.class).setParameter("accesstoken", accesstoken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserAuthEntity updateUserAuth(UserAuthEntity userAuthEntity) {
        return entityManager.merge(userAuthEntity);
    }

    public UserAuthEntity updateAuthToken(final UserAuthEntity userAuthTokenEntity){
        entityManager.merge(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public UserAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

    public UserEntity getUser(final String userUuid){
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();
        }
        catch(NoResultException nre){
            return null;
        }
    }
}
