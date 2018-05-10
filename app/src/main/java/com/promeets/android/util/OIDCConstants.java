package com.promeets.android.util;

/**
 * Created by sosasang on 3/31/17.
 */

/*
These constant values configure the client app to use OAuth2 and open id connect
to authenticate with Azure and authorize the app to access the specified scopes.
Read more about scopes: https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-v2-scopes
 */
interface OIDCConstants {
    String AUTHORITY_URL = "https://login.microsoftonline.com/common";
    // Update these two constants with the values for your application:
    String CLIENT_ID = "996a3eba-e371-4158-b48a-bdba08b104b6";
    String REDIRECT_URI = "https://login.microsoftonline.com/common/oauth2/nativeclient";
    String MICROSOFT_GRAPH_API_ENDPOINT_RESOURCE_ID = "https://graph.microsoft.com/";
    //String SCOPES = "openid Calendars.Read offline_access";
    String SCOPES = "openid https://outlook.office.com/Calendars.Read offline_access";
}
