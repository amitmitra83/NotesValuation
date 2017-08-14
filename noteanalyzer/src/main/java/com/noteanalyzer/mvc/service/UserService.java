package com.noteanalyzer.mvc.service;

import java.util.Optional;

import com.noteanalyzer.mvc.model.UserModel;

/**
 * This class is responsible for all kind of business logic for user releated
 * activities.
 * 
 * @author Arvind Ray
 *
 */

public interface UserService {

	/**
	 * This method will create an user with given user details.
	 * 
	 * @param user
	 */
	public void createUser(UserModel user);

	/**
	 * This method will return an user object for given user name.
	 * 
	 * @param userName
	 * @return
	 */
	public Optional<UserModel> getByUsername(String userName);

	/**
	 * This method will reset the user password.
	 * 
	 * @param userName
	 * @return
	 */
	public Optional<UserModel> resetUserPassword(String userName);

	/**
	 * This method will update the user details with given user input.
	 * 
	 * @param user
	 * @return
	 */
	public Optional<UserModel> updateUser(UserModel user);

	/**
	 * This method will change the password for valid reset token.
	 * 
	 * @param inputUser
	 * @param isResetTokenRequired
	 * @return
	 */
	Optional<UserModel> changePassword(UserModel inputUser);

	/**
	 * This method wil be used to verify and user and make the status as ACTIVE.
	 * 
	 * @param inputUser
	 * @return
	 */
	public Optional<UserModel> verifyUser(UserModel inputUser);

	/**
	 * This method will return the status of given user.
	 * 
	 * @param userName
	 * @return
	 */
	public String getUserStatus(String userName);

	/**
	 * This method will keep updating the unsuccessfull login attempts at every
	 * login.
	 * 
	 * @param loginStatus
	 * @param userId
	 * @param userEmailId
	 */
	void updateUnsuccessfullAttempt(String loginStatus, long userId, String userEmailId);

	
	/**
	 * This method will be used for changing the password for logged in user.
	 * @param inputUser
	 * @return
	 */
	public Optional<UserModel> changePasswordForLoginUser(UserModel inputUser);

}
