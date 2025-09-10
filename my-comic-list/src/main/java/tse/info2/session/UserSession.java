package tse.info2.session;

import tse.info2.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private boolean isGuest = false;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
        this.isGuest = false;
    }

    public User getUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public void clearSession() {
        currentUser = null;
        isGuest = false;
    }

    public void setAsGuest() {
        this.isGuest = true;
        this.currentUser = null;
    }

    public boolean isGuest() {
        return isGuest;
    }
}
