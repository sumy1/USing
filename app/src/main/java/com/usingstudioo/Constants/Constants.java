package com.usingstudioo.Constants;

public interface Constants {

    /*****************************Public Static Constant and Keys**********************************/

    String kDefaultAppName = "TUDO";
    String kIsFirstTime = "isFirstTime";
    String kApiVersion = "apiVersion";
    String kAppPreferences = "TudoPreferences";
    String kImageBaseUrl = "https://vibestest.com/wip_projects/development/socialsportz/api/assets/public/images/";

    /**********************API RequestParameters And ResponseParameters****************************/

    int PERMISSIONS_REQUEST = 100;
    int REQUEST_PERMISSIONS_RECORD = 103;
    int ACTIVITY_TYPE_RESULT = 101;

    String KPurchageToken="purchaseToken";

    //API Base Key
    String kId = "id";
    String kPageId = "page_id";
    String kStatus = "status";
    String kTag = "tag";
    String kMessage = "message";
    String kResponse = "response";
    String kResponseMessage = "response_messege";
    String kSignUp = "Please signup";
    String kAction = "action";
    String kSuccess = "success";
    String kResult = "result";
    String kRecords = "records";
    String kRecord  ="record";
    String kData = "data";
    String kType = "type";
    String kPage = "page";
    String kCurrentPage = "current_page";
    String kFlag = "flag";
    String kTitle = "title";
    String kImage = "image";
    String kLimit = "limit";
    String kRange = "range";
    String kScale = "scale";
    String kResults = "results";
    String kTotalRecords = "totalRecords";
    String kDescription = "description";
    String kFolderName = "folder_name";
    String kU8Format = "UTF-8";

    String welcomeScreenShownPref = "welcomeScreenShown";

    String kSeperator = "__";
    String kEmptyString = "";
    String kWhitespace = " ";
    Number kEmptyNumber = 0;

    // Error Messages
    String kMessageInternalInconsistency = "Some internal inconsistency occurred.\nPlease try again.";
    String kMessageNetworkError = "Device does not connect to internet.";
    String kSocketTimeOut = "Using Server not responding...";
    String kMessageServerNotRespondingError = "Using server not responding!";
    String kDataEmptyMessage = "Record empty please insert data";
    String kDataAlreadySubmitted = "Record already save";
    String kRecordSucsessfull = "Record insert successfully";
    String kAlreadyLogin = "Already login";
    String kAlreadyLogout = "Already logout";
    String kMessageConnecting = "Connecting...";
    String kError = "Error";
    String kLocationPermisionMsg = "You have denied the access permission permanently, allow the permission from setting.";

    String kCurrentTimeStamp = "CurrentTimeStamp";
    String kTimestamp = "timeStamp";

    // Types
    String kCurrentUser = "currentUser";
    String kAuthToken = "authtoken";
    String kGenericAuthToken = "genericAuthToken";
    String kDefaultAuthToken = "defaultAuthToken";

    // User
    String kUserId="user_id";
    String kName = "name";
    String kFirstName = "fname";
    String kLastName = "lname";
    String kUserName = "username";
    String kFullName = "fullname";
    String kPassword = "password";
    String kNewPassword = "newpassword";
    String kOldPassword = "oldpassword";
    String kEmail = "email";
    String kMobile = "mobile";
    String kGender = "gender";
    String kPhone = "phone";
    String kAddress = "address";
    String kProfileImage = "image";
    String kDeviceId="device_id";
    String kDeviceToken = "device_token";
    String kDeviceReg = "devicetoken";
    String kAgreeTC = "agreetnc";
    String kAgreeTNC = "agree_tnc";
    String kOtp="otp";
    String kLoginType="login_type";
    String kUserType="key";
    String kIsOTPVerified = "otp_verify";
    String kIsEmailVerified = "is_email_verified";
    String kIsRegisterVerified = "registar_otp_verify";
    String kUserStatus = "user_status";
    String kProfile = "user_profile_image";
    String kEmailVerify = "email_verification";
    String kRegisteredEmail = "register_email";
    String kHashId="hash_id";
    String kDate = "date";
    String kCreatedOn = "createdon";
    String kUpdateOn = "updatedon";
    String kIsUserActive = "isActive";

    String kEditProfile = "Edit Profile";
    String kMySubscription="My Subscription";
    String kGotoMenu = "Go to Main Menu";
    String kRecordingList= "Recording List";
    String kChangePassword= "Change Password";
    String kLogout= "Logout";

    String kSongId = "songId";
    String kSongKey = "songKey";
    String kSongArtist = "songArtist";
    String kSongTitle = "songTitle";
    String kSongType = "songType";
    String kSongStatus = "songStatus";
    String kYoutubeLink = "youtubeLink";
    String kLinkFireLink = "linkfirelink";
    String kEmbeddedLink="embedded_link";
    String kAudioVideo="audio_video";

    String kTempo = "tempo";
    String kPPQN = "ppqn";
    String kNotes = "notes";
    String kStartTime = "start_time";
    String kNoteName = "note_name";
    String kNoteStatus = "note_status";

    String kRangeName = "rangeName";
    String kScaleName = "scaleName";


    //add plan..
    String kPlanType="plan_type";
    String kPlanStartDate="plan_start_date";

    //Ened add plan

    // Facebook Constants
    String kFacebookFields = "fields";
    String kFacebookAllFields = "id,name,link,email,picture,first_name,last_name,gender";

    String kFacebookId = "id";
    String kFacebookEmail = "email";
    String kFacebookFirstName = "first_name";
    String kFacebookLastName = "last_name";
    String kFacebookGender = "gender";

    //Google map api constants
    String kPremise = "premise";
    String kStreetNumber = "street_number";
    String kRoute = "route";
    String kLocality = "locality";
    String kAdministrativeAreaLevel2 = "administrative_area_level_2";
    String kAdministrativeAreaLevel1 = "administrative_area_level_1";
    String kPostalCode = "postal_code";
    String kGCountry = "country";
    String kimage= "image";

   /**
     * Http Status for API Response
     */
    enum HTTPStatus {
        success(1),
        badRequest(400),
        unauthorized(401),
        notFound(404),
        methodNotAllowed(405),
        notAcceptable(406),
        proxyAuthenticationRequired(407),
        requestTimeout(408),
        error(-100);         //No option found.

        //Definition
        private int httpStatus;

        HTTPStatus(int httpStatus) {
            this.httpStatus = httpStatus;
        }

        public static HTTPStatus getStatus(int status) {
            for (HTTPStatus httpStatus : HTTPStatus.values()) {
                if (httpStatus.httpStatus == status) {
                    return httpStatus;
                }
            }
            return error;
        }

        public Integer getValue() {
            return this.httpStatus;
        }
    }

    /**
     * Status Enumeration for Task Status
     */
    enum Status {
        success(1),
        fail(0),
        reachLimit(2),
        noChange(3),
        history(4),            //If xmpp message is history
        normal(5),            //If Normal xmpp message
        discard(6);

        //Definition
        private int value;

        Status(int status) {
            this.value = status;
        }

        public static Status getStatus(int value) {
            for (Status status : Status.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return fail;
        }

        /**
         * To get Integer value of corresponding emum
         */
        public Integer getValue() {
            return this.value;
        }

    }


    /**
     * PlanType Enumeration for facility/Academy types
     */
    enum PlanType {
        Supreme(3),
        Ultimate(2),
        Starter(1);

        //Definition
        private int value;

        PlanType(int type) {
            this.value = type;
        }

        public static PlanType getPlanType(int value) {
            for (PlanType status : PlanType.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return Starter;
        }

        /**
         * To get Integer value of corresponding emum
         */
        public Integer getValue() {
            return this.value;
        }

    }

    /**
     * LoginType Enumeration for user login scenario
     */
    enum LoginType {
        Normal(1),
        Facebook(2),
        Google(3);

        //Definition
        private int value;

        LoginType(int status) {
            this.value = status;
        }

        public static Constants.LoginType getLoginType(int value) {
            for (Constants.LoginType status : Constants.LoginType.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return Normal;
        }

        /**
         * To get Integer value of corresponding emum
         */
        public Integer getValue() {
            return this.value;
        }

    }

    /**
     * PaymentStatus Enumeration for any type of status
     */
    enum PaymentStatus {
        PaymentDone(0),
        PaymentPending(1);

        //Definition
        private int value;

        PaymentStatus(int status) {
            this.value = status;
        }

        public static Constants.PaymentStatus getPaymentStatus(int value) {
            for (Constants.PaymentStatus status : Constants.PaymentStatus.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return PaymentDone;
        }

        /**
         * To get Integer value of corresponding emum
         */
        public Integer getValue() {
            return this.value;
        }

    }

}
