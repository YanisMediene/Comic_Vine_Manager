package tse.info2.controller;
import tse.info2.model.Issue;
import tse.info2.service.UserService;

public class UserController {
	  private final UserService userService;

	    public UserController(UserService userService) {
	        this.userService = userService;
	    }
  
    
    public boolean addIssueToFavorites(int userId, String issueApiDetailUrl) {
        return userService.addIssueToFavorites(userId, issueApiDetailUrl);
    }

}
