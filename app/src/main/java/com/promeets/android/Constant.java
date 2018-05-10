package com.promeets.android;

public interface Constant {

    final String BASE_URL = "https://home.promeets.us/api/";
    //final String BASE_URL = "https://api.promeets.us/";
    //final String BASE_URL = "http://192.168.1.103:8080/";

    enum ServiceType {
        NORMAL_USER_LOGIN,
        USER_REGISTRATION,
        USER_VERIFICATION,
        USER_FORGET_PASSWORD_CODE,
        USER_SAVE_NEW_PASSWORD,
        HOME_PAGE_BANNER,
        HOME_PAGE_GRID,
        HOME_PAGE_ADVERTISEMENT,
        RECOMMNDED_LIST,
        SUBCATEGORY_LIST,
        INDUSTRY_LIST,
        EXPERT_PROFILE_DETAIL,
        USER_PROFILE_DETAIL,
        EXPERT_LIKE,
        CHECK_FOR_UPDATE,
        CHECK_FOR_CAMPAIGN,
        UNREAD_MSG_COUNT,
        SYNC_TIME,
        WISH_LIST,
        REVIEW_LIST,
        POST_REVIEW,
        USEFUL_REVIEW,
        DELETE_REVIEW,
        USER_DASHBOARD,
        EXPERT_DASHBOARD,
        REFERRAL_IMAGE
    }

    final String RELOGIN_ERROR_CODE = "2000";
    final String UPDATE_TIME_STAMP = "2001";
    final String UPDATE_THE_APPLICATION = "2002";

    final String CONTENT_TYPE = "Content-type";
    final String CONTENT_TYPE_VALUE = "application/json";
    final String PROMEETS_SCREEN_WIDTH = "promeets_screen_width";
    final String PROMEETS_SCREEN_HEIGHT = "promeets_screen_height";
    final String DEVICE_ID="deviceId";


    final String LOGIN_OPERATION ="login";
    final String LOGOUT_OPERATION ="logout";
    final String POST_REPLY ="review/expertReply";
    final String MODIFY_REPLY = "review/modifyReply";
    final String USEFUL_REVIEW = "review/usefulRating";
    final String DELETE_REVIEW = "review/delete";
    final String CHAT_SERVICE_LIST  = "hyphenateChat/checkChatFlag";
    final String CHAT_USER_INFO = "hyphenateChat/getUserInfo";
    //final String CITY_OPERATION = "recommend/fetchByCityName";
    final String CITY_OPERATION = "recommend/fetchByCityNameV2";
    final String MENU_OPERATION = "contentCategory/fetchAllV2";
    final String ADVERTISEMENT = "advertisement/fetch";
    final String NOTIFICATION_OPERATION = "notification/getUnreadMsgCount";
    final String USER_REGISTERATION = "register/getRegisterVerifyCode";
    final String USER_REGISTERATION_CHECK_CODE = "register/checkVerifyCode";
    final String USER_REGISTERATION_SUBMIT = "register/setupV2";
    final String PASSWORD_VERIFY_CODE = "register/getResetPasswordVerifyCode";
    final String PASSWORD_REGISTER_NEW = "register/resetPassword";
    final String PASSWORD_RESET = "register/resetPasswordAfterLogin";
    final String RECOMMENDED_LIST_OPERATION = "homepageservice/fetchExpertServicePage";
    final String GET_SUBCATEGORY_OPERATION = "subContentCategory/fetchAllSubContentCategoryByParentId";
    final String SEARCH_BY_INDUSTRY = "subContentCategory/fetchByIndustryId";
    final String SEARCH_BY_NUMBER_OF_MEETINGS = "MeetTheMost";
    final String SEARCH_BY_HIGHEST_RATING = "HighestScore";
    final String SEARCH_BY_LOWEST_PRICE = "TheLowestPrice";
    final String SEARCH_BY_COMPREHENSIVE = "ComprehensiveRanking";
    final String FILTER_BY_RANGE = "homepageservice/findByPriceBetween";
    final String FILTER_ABOVE = "homepageservice/findByPriceAfter";
    final String DISCOVER_PAGE_OPERATION = "homepageservice/fetchExpertServicePage";
    final String REVIEW_LIST_OPERATION = "homepageservice/fetchReviewList";
    final String EVENT_REVIEW_DETAIL = "activeEvent/displayReviewDetail";
    final String FETCH_EXPERT_DETAIL = "homepageservice/fetchExpertDetail";
    final String FETCH_SERVER_TIME = "time/syncTime";
    final String FETCH_MY_EXPERT_PROFILE = "expertprofile/fetchIncludeService";
    final String FETCH_MY_WISH_LIST = "wishlist/display";
    final String FETCH_MY_USER_PROFILE = "userprofile/fetch";
    final String CREATE_CUSTOMER_ISSUE = "customerIssue/create";
    final String UPDATE_EXPERT_LIKE = "homepageservice/expertLike";
    final String CANCEL_MEETING_REQUEST = "eventrequest/userCanceled";
    final String FORCE_CANCEL_MEETING_REQUEST = "eventrequest/userForceCancel";
    final String CHECK_FOR_UPDATE = "version/check";
    final String CHECK_FOR_CAMPAIGN = "campaigns/fetchByUuid";
    final String LOGIN_OTHER = "login/other";
    final String FILTER_AND_SORT = "homepageservice/findBySortAndFilter";

    final String ACCOUNT_NUMBER = "accountNumber";
    final String PASSWORD = "password";
    final String LOCATION_URL = "http://maps.google.com/maps/api/geocode/json?address=";
    final String CITYNAME = "cityName";
    final String SUBCATEGORY = "parentId";
    final String INDUSTRY = "industryId";
    final String FIRST_PRICE = "fprice";
    final String LAST_PRICE = "lprice";
    final String PRICE = "price";
    final String PAGENUMBER = "pageNumber";
    final String EXPERTID = "expId";
    final String USERID = "userId";
    final String SERVICEID = "serviceId";
    final String MYID = "id";
    final String EVENTID="eventId";
    final String TIMEZON = "timeZone";
    final String PLATFORM = "platform";
    final String PLATFORM_TOKEN = "platformToken";
    final String USER_NAME = "userName";
    final String CITY = "city";
    final String EMPLOYER = "employer";
    final String PHOTO_URL = "photoURL";
    final String RELEASE_EMAIL = "releaseEmail";
    final String POSITION = "position";
    final String RELEASE_PHONE_NUMBER = "releasePhoneNumber";
    final String FULLNAME = "fullName";
    final String VERSION = "version";
    final String UUID = "uuid";
    final String SORTKEY = "sortKey";
    final String SORTKEY_MOST_USEFUL = "mostUseful";
    final String SORTKEY_MOST_RECENT = "mostRecent";
    final String VIEW_ID = "viewId";
    final String USER_DASHBOARD_DISPLAY = "userDashboard/display";
    final String EXPERT_DASHBOARD_DISPLAY = "expertDashboard/display";
    final String FETCH_REFERRAL_IMAGE = "referral/imgurl";
}
