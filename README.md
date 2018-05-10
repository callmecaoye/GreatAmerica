# ![Promeets](https://promeets.us/web/home/assets/img/logo_new.png)
# Promeets Android Development Documentation

Website: <https://promeets.us>

project link: <https://github.com/PromeetsDev/PromeetsDev_Android>

## 1. Module

### 1.1 Login
<span style="color:green"> MainActivity </span>: entrance for login module. We start MainActivity when we need user info to do certain action;

- login

    <span style="color:red"> *After integration with 3rd party, call loginOther* </span>

    <span style="color:red"> *After login, save related user information to SharedPreference* </span>
    + Normal user login: Email/phone + password
    + Facebook Integration: <https://developers.facebook.com/docs/facebook-login/android>
    + LinkedIn Integration:
    
        --- LinkedIn apk auth: <https://developer.linkedin.com/docs/android-sdk>
    
        --- LinkedIn web auth: <span style="color:green"> AuthActivity</span>
- signup / forget password 
<span style="color:red"> *After log in, save related user data to SharedPreference* </span>
Email/phone -> Code -> Passowrd
- reset password
- log out
<span style="color:red"> *After log out, clear user data from SharedPreference* </span>

### 1.2 Profile
- UserPOJO
Include user id, user name, password, accessToken etc.
- User Profile
Include user info, industry, polling, expert status etc.
- Expert Profile
Mainly used for <span style="color:green"> ExpertDetailActivity </span> and <span style="color:green"> ExpertDashboarActivity </span>
Include expert info, service, availability, time & location etc.
- Expert service
Include expert id, service info etc.

### 1.3 Become Expert
<span style="color:green"> BecomeExpActivity </span>

 - Upload photo from Camera/Gallery
 - Import profile from LinkedIn

### 1.4 Appointment
 - make appointment

    <span style="color:green"> UserRequestSettingActivity </span>

    Include service info, time & location, Question & About me

 - appointment status
 
    <span style="color:green"> AppointStatusActivity </span>

    Include service info, time & location, Question & About me, order info, review, payment etc.

    <span style="color:red"> *Used displayStep to control UI visibility* </span>

    <span style="color:green"> PaymentActivity </span>

    Include price calculation(balance, promotion) and Credit Card integrated with Stripe.

### 1.5 Event
<span style="color:green"> EventFragment </span> in <span style="color:green"> HomeActivity </span>

- event detail
different UI visibility for upcoming and past events
- event review
user/guest

### 1.6 Message
- Notification
GCM Broadcast receiver in package <span style="color:green"> com.promeets.android.notification </span>

- Chat
<span style="color:green"> ChatActivity </span>



## 2. Custom View

### PromeetsBottomBar
<span style="color:green"> HomeActivity </span> fragments navigation

### PromeetsDialog
Polymorphism for different usages

### CategoryView & PollingView
Category item view for dynamic height



## 3. Third-party Integration

### 3.1 Chat: SendBird
<http://docs.sendbird.com/android>

### 3.2 Facebook login integration(Log in)

### 3.3 LinkedIn login integration(Log in)

### 3.4 Sync Google Calendar(Expert availability)
<https://developers.google.com/google-apps/calendar/quickstart/android>

### 3.5 Sync Outlook Calendar(Expert availability)
<span style="color:green"> com.promeets.android.util.AuthenticationManager + AuthenticationCallback </span> along with <span style="color:green"> oidclib  </span> package

### 3.6 Time picker from calendar view
https://github.com/alamkanak/Android-Week-View

<span style="color:green"> CalendarViewActivity </span>: need to parse between EventTimePOJO and WeekViewEvent 

### 3.7 Google mapView within Marker
<https://developers.google.com/maps/documentation/android-api/start>

### 3.8 Location picker from Google PlacePicker
<https://developers.google.com/places/android-api/placepicker>



## 4. Issues

### 4.1 UI
<span style="color:red">**TODO: Material_Animation**</span>

<span style="color:red">**TODO: follow Material Design**</span>

<span style="color:green">**DONE: General Progress dialog**</span>

###  4.2 Permission 
<span style="color:green">**DONE: AndPermission**</span>
<https://github.com/yanzhenjie/AndPermission>

### 4.3 EditText & Keyboard
Activity is fixed with AndroidBug5497Workground
Fragment is not working, replace fragment with View


