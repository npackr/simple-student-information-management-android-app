package bomoncntt.svk60.btl1851062658.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.UUID;

public class LoginAPI extends SQLiteOpenHelper {
    public static String DB_NAME = "UserDatabase";
    public static String DB_TABLE_NAME = "User";
    public static String DB_PROPERTY_ID = "id";
    public static String DB_PROPERTY_USERNAME = "username";
    public static String DB_PROPERTY_PASSWORD = "password";
    public static String DB_PROPERTY_USERID = "userid";
    public static String DB_PROPERTY_LOGIN = "session";

    public LoginAPI(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1, null);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_TABLE_NAME + "("+ DB_PROPERTY_ID +" TEXT primary key, "+ DB_PROPERTY_USERNAME +" TEXT, " + DB_PROPERTY_PASSWORD + " TEXT, "+ DB_PROPERTY_USERID +" TEXT, " + DB_PROPERTY_LOGIN+ " TEXT)");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(DB_TABLE_NAME, null, null);
        onCreate(db);
    }

    public boolean loginByUsername(String username, String userpassword) {
        SQLiteDatabase l_odb = this.getReadableDatabase();
        Cursor cs = l_odb.query(false, DB_TABLE_NAME, new String[]{DB_PROPERTY_USERNAME, DB_PROPERTY_PASSWORD}, DB_PROPERTY_USERNAME + "=?" + " and " + DB_PROPERTY_PASSWORD + "=?", new String[]{username, userpassword}, null, null, null, "1");
        if (cs.getCount() > 0) {
            cs.close();
            return true;
        } else {
            cs.close();
            return false;
        }
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase l_odb = this.getReadableDatabase();
        Cursor cs = l_odb.query(false, DB_TABLE_NAME, new String[]{DB_PROPERTY_USERNAME}, DB_PROPERTY_USERNAME + "=?", new String[]{username}, null, null, null, "1");
        if (cs.getCount() > 0) {
            cs.close();
            return true;
        } else {
            cs.close();
            return false;
        }
    }

    public boolean checkLoginToken(String username, String token) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cs = database.query(false, DB_TABLE_NAME, new String[]{DB_PROPERTY_USERNAME, DB_PROPERTY_LOGIN}, DB_PROPERTY_USERNAME + "=?" + " and " + DB_PROPERTY_LOGIN + "=?", new String[]{username, token}, null, null, null, "1");
        if (cs.getCount() > 0) {
            cs.close();
            return true;
        } else {
            cs.close();
            return false;
        }
    }

    public String setNewLoginToken(String username, String userpassword) {
        SQLiteDatabase database = this.getWritableDatabase();
        if (loginByUsername(username, userpassword)) {
            ContentValues l_newdata = new ContentValues();
            l_newdata.put(DB_PROPERTY_LOGIN, UUID.randomUUID().toString().substring(0, 16));
            database.update(DB_TABLE_NAME, l_newdata, DB_PROPERTY_USERNAME + "=?", new String[]{username});
            return l_newdata.getAsString(DB_PROPERTY_LOGIN);
        } else {
            ContentValues l_newdata = new ContentValues();
            l_newdata.put(DB_PROPERTY_LOGIN, (String) null);
            database.update(DB_TABLE_NAME, l_newdata, DB_PROPERTY_USERNAME + "=?", new String[]{username});
            return "";
        }
    }

    public boolean clearLoginToken(String username, String token) {
        if (checkLoginToken(username, token)) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues l_newdata = new ContentValues();
            l_newdata.put(DB_PROPERTY_LOGIN, (String) null);
            database.update(DB_TABLE_NAME, l_newdata, DB_PROPERTY_USERNAME + "=?", new String[]{username});
            return true;
        } else {
            return false;
        }
    }

    public long createDemoUser() {
        if (checkUserExists("1851062658")) {
            return -1;
        } else {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DB_PROPERTY_ID, "1");
            cv.put(DB_PROPERTY_USERNAME, "1851062658");
            cv.put(DB_PROPERTY_PASSWORD, "123");
            cv.put(DB_PROPERTY_USERID, "Nguyen Anh Kiet");
            cv.put(DB_PROPERTY_LOGIN, "");
            return database.insertOrThrow(DB_TABLE_NAME, null, cv);
        }
    }
}
