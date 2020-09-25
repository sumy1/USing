package com.usingstudioo.Database;

public interface DBConstants {
    //String DB_PATH = Environment.getExternalStoragePublicDirectory("").getAbsolutePath()+File.separator;
    String DB_FOLDER = "SocialSportzDatabase";
    int DB_VERSION = 1;

    String DATABASE_NAME = "SocialSportz";
    String TABLE_USER_PROFILE = "UserProfile";   // Table Name
    String TABLE_FACILITY_PROFILE = "FacilityProfile";
    String TABLE_SPORT_PROFILE = "SportProfile";

    //Fields for the user table
    String KEY_USER_ID = "userId";
    String KEY_USER_NAME = "username";
    String KEY_USER_IMG = "profileImg";
    String KEY_USER_GENDER = "gender";
    String KEY_USER_EMAIL = "email";
    String KEY_EMAIL_VERIFIED = "emailVerified";
    String KEY_USER_PHONE = "contact";
    String KEY_PHONE_VERIFIED = "phoneVerified";
    String KEY_USER_CITY = "city";
    String KEY_USER_ADDRESS = "address";
    String KEY_USER_PINCODE = "pincode";
    String KEY_USER_LATITUDE = "latittude";
    String KEY_USER_LONGITUDE = "longitude";
    String KEY_USER_PASSWORD = "reset_password";
    String KEY_USER_STATUS = "status";

    //Fields for the facility table
    String KEY_FAC_ID = "facId";
    String KEY_FAC_TYPE = "facType";
    String KEY_FAC_NAME = "facName";
    String KEY_FAC_SHORT_DESC = "facShortDescription";
    String KEY_FAC_LOGO = "facLogo";
    String KEY_FAC_CITY = "facCity";
    String KEY_FAC_ADDRESS = "facAddress";
    String KEY_FAC_PINCODE = "facPincode";
    String KEY_FAC_LATITUDE = "facLatittude";
    String KEY_FAC_LONGITUDE = "facLongitude";
    String KEY_FAC_DESC = "facDescription";
    String KEY_FAC_ACH = "facAchievement";
    String KEY_FAC_OPEN_TIMING = "facOpenTime";
    String KEY_FAC_CLOSE_TIMING = "facCloseTime";
    String KEY_FAC_BANNER = "facBanner";
    String KEY_FAC_STATUS = "facStatus";

    //Fields for the facility gallery
    String KEY_GALLERY1 = "gallery1";
    String KEY_GALLERY2 = "gallery2";
    String KEY_GALLERY3 = "gallery3";
    String KEY_GALLERY4 = "gallery4";
    String KEY_GALLERY5 = "gallery5";
    String KEY_GALLERY6 = "gallery6";

    //Fields for the Sport List
    String KEY_SPORT_ID = "sportId";
    String KEY_SPORT_NAME = "sportName";
    String KEY_SPORT_DESC = "sportDesc";
    String KEY_SPORT_IMG = "sportImg";
    String KEY_SPORT_STATUS = "sportStatus";

    //Fields for the facility Sport
    String KEY_FAC_SPORT_ID = "facSportId";
    String KEY_SPORT_COURT = "facSportCourt";
    String KEY_SPORT_INDOOR = "facSportIndoor";
    String KEY_SPORT_OUTDOOR = "facSportOutdoor";
    String KEY_SPORT_KIT = "facSportKit";

    //Fields for the user Sport Interest
    String KEY_USER_INTEREST_ID = "userInterestId";
    String KEY_USER_SPORT_ID = "userSportId";
    String KEY_USER_KEY_ID = "userId";

    //Fields for the facility Amenity
    String KEY_AMENITY_ID = "amenityId";
    String KEY_AMENITY_NAME = "amenityName";
    String KEY_AMENITY_DESC = "amenityDesc";
    String KEY_AMENITY_FAC_ID = "amenityFacId";
    String KEY_AMENITY_QUANTITY = "amenityQuantity";

    //Fields for the

}
