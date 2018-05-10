package com.promeets.android.listeners;

import android.support.v4.app.Fragment;

/**
 * Listener for navigate fragments to activity
 */
public interface OnScreenChangeListener {

    enum SCREEN {
        HOME_SCREEN_FRAGMENT,
        ACCOUNT_SCREEN_FRAGMENT,
        CUSTOM_MESSAGE_DIALOG,
        VERIFY_USER_REGISTERATION,
        SHOW_ALL_CATEGORY_MENU,
        SERVIE_DETAIL_POPUP,
        SERVICE_SEARCH_FRAGMENT,
        NOTIFICATION_LIST_FRAGMENT,
        EVENT_FRAGMENT
    };
    public <T> Fragment onScreenChange(int containerId, SCREEN fragmentName, String fragmentTag, boolean addTobackStack, Object data);
}
