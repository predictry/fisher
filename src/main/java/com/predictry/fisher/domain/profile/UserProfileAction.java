package com.predictry.fisher.domain.profile;

/**
 * Enumeration of available actions for user profile.
 */
public enum UserProfileAction {
    BUY, VIEW;

    static public boolean contains(String actionName) {
        UserProfileAction[] actions = UserProfileAction.values();
        for (UserProfileAction action: actions) {
            if (actionName.equals(action.name())) {
                return true;
            }
        }
        return false;
    }
}
