package com.usingstudioo.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper implements DBConstants {
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PROFILE
                + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_NAME + " VARCHAR(100),"
                + KEY_USER_GENDER + " VARCHAR(10),"
                + KEY_USER_EMAIL + " VARCHAR(255),"
                + KEY_USER_PHONE + " VARCHAR(100),"
                + KEY_USER_ADDRESS + " VARCHAR(255),"
                + KEY_USER_CITY + " VARCHAR(100),"
                + KEY_USER_PINCODE + " VARCHAR(10),"
                + KEY_USER_LATITUDE + " VARCHAR(10),"
                + KEY_USER_LONGITUDE + " VARCHAR(10),"
                + KEY_USER_PASSWORD + " VARCHAR(100)"
                + ")";

        String CREATE_TABLE_FACILITY = "CREATE TABLE IF NOT EXISTS " + TABLE_FACILITY_PROFILE
                + "("
                + KEY_FAC_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_FAC_TYPE + "VAr ,"
                + KEY_FAC_ID + " VAR PRIMARY KEY,"
                + KEY_FAC_NAME + " VARCHAR(255),"
                + KEY_FAC_BANNER + " VARCHAR(255),"
                + KEY_FAC_DESC + " VARCHAR(1000),"
                + KEY_FAC_ACH + " VARCHAR(10),"
                + KEY_FAC_OPEN_TIMING + " VARCHAR(25),"
                + KEY_FAC_CLOSE_TIMING + " VARCHAR(25)"
                + ")";

        /*String CREATE_TABLE_SPORT = "CREATE TABLE IF NOT EXISTS " + TABLE_SPORT_PROFILE
                + "("
                + KEY_Sp + " INTEGER,"
                + KEY_SUBCATEGORY_ID + " INTEGER,"
                + KEY_CHILDCATEGORY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CHILDCATEGORY_NAME + " VARCHAR(255)"
                + ")";*/


        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FACILITY);
        //db.execSQL(CREATE_TABLE_SPORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACILITY_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPORT_PROFILE);

        // Create tables again
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
/*
    //Add the values in category in table
    public void addCategory(SubCategory model)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, model.getSubCategoryId());
        values.put(KEY_CATEGORY_NAME,model.getSubCategoryName());
        db.insertWithOnConflict(TABLE_CATEGORY, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    //Add the values in subcategories in table
    public void addSubCategory(SubCategory model)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, model.getCategoryId());
        values.put(KEY_SUBCATEGORY_ID, model.getSubCategoryId());
        values.put(KEY_SUBCATEGORY_NAME,model.getSubCategoryName());
        db.insertWithOnConflict(TABLE_CATEGORY, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    //Add the values in subcategories in table
    public void addChildCategory(ChildCategory model)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, model.getCategoryId());
        values.put(KEY_SUBCATEGORY_ID, model.getSubCategoryId());
        values.put(KEY_CHILDCATEGORY_ID, model.getChildCategoryId());
        values.put(KEY_CHILDCATEGORY_NAME,model.getChildCategoryName());
        db.insertWithOnConflict(TABLE_CATEGORY, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    //Add the values in subcategories in table
    public void addCategoryStatus(CategoryStatus model)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHILDCATEGORY_ID, model.getChildCategoryId());
        values.put(KEY_USER_ID, model.getUserId());
        values.put(KEY_CHILDCATEGORY_STATUS,model.getCategoryStatus());
        values.put(KEY_CHILDCATEGORY_MSG,model.getCategoryMessage());
        values.put(KEY_CHILDCATEGORY_DATE,model.getSdate());
        db.insertWithOnConflict(TABLE_CAT_STATUS, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }


    //method to get categories data from database.
    public List<Category> getCategoryList(String userId){
        Cursor c;
        List<Category> categoryList = new ArrayList<>();
        try{
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE userId = " + userId,null);
            if(c!= null){
                System.out.println("Values::::" + c.getCount());
                if(c.moveToFirst()){
                    do{
                        Integer id = c.getInt(c.getColumnIndex(KEY_CATEGORY_ID));
                        String name = c.getString(c.getColumnIndex(KEY_CATEGORY_NAME));
                        categoryList.add(new Category(id, name));
                    }
                    while (c.moveToNext());
                }
                c.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return categoryList;
    }

    //method to get subcategories data from database.
    public List<SubCategory> getSubCategoryList(String userId){
        Cursor c;
        List<SubCategory> subcategoryList = new ArrayList<>();
        try{
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_SUB_CATEGORY + " WHERE userId = " + userId,null);
            if(c!= null){
                System.out.println("Values::::" + c.getCount());
                if(c.moveToFirst()){
                    do{
                        Integer super_id = c.getInt(c.getColumnIndex(KEY_CATEGORY_ID));
                        Integer id = c.getInt(c.getColumnIndex(KEY_SUBCATEGORY_ID));
                        String name = c.getString(c.getColumnIndex(KEY_SUBCATEGORY_NAME));
                        subcategoryList.add(new SubCategory(super_id,id, name));
                    }
                    while (c.moveToNext());
                }
                c.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return subcategoryList;
    }

    //method to get childcategories data from database.
    public List<ChildCategory> getChildCategoryList(String userId){
        Cursor c;
        List<ChildCategory> childcategoryList = new ArrayList<>();
        try{
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_SUB_CATEGORY + " WHERE userId = " + userId,null);
            if(c!= null){
                System.out.println("Values::::" + c.getCount());
                if(c.moveToFirst()){
                    do{
                        Integer super_id = c.getInt(c.getColumnIndex(KEY_CATEGORY_ID));
                        Integer sub_id = c.getInt(c.getColumnIndex(KEY_SUBCATEGORY_ID));
                        Integer id = c.getInt(c.getColumnIndex(KEY_CHILDCATEGORY_ID));
                        String name = c.getString(c.getColumnIndex(KEY_CHILDCATEGORY_NAME));
                        childcategoryList.add(new ChildCategory(super_id,sub_id,id, name));
                    }
                    while (c.moveToNext());
                }
                c.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return childcategoryList;
    }*/


}
