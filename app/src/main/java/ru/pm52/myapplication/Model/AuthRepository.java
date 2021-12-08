package ru.pm52.myapplication.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.pm52.myapplication.App;
import ru.pm52.myapplication.DBHelper;
import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.ICallbackResponse;
import ru.pm52.myapplication.INotify;
import ru.pm52.myapplication.ViewModel.AuthViewModel;

public class AuthRepository implements INotify {

    public static String NAME_COLUMN_DATABASE = "DATABASE";
    public static String NAME_COLUMN_SERVER = "SERVER";
    public static String NAME_COLUMN_USERNAME = "USERNAME";

    @Nullable
    private INotify objectNotify;

    @Nullable
    private static AuthRepository instance;

    private DBHelper dbHelper;
    private AuthModel model = new AuthModel();

    private AuthRepository() {
        dbHelper = new DBHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT MAX(T.DB) AS DB, MAX(T.Server) AS Server, MAX(T.Username) as Username " +
                "FROM (" +
                "   SELECT T.Value AS DB, \"\" AS Server, \"\" as Username FROM SETTINGS AS T WHERE T.NAME = ?" +
                "   UNION ALL" +
                "   SELECT \"\", T.Value, \"\" FROM SETTINGS AS T WHERE T.NAME = ?" +
                "   UNION ALL " +
                "   SELECT \"\", \"\", T.Value FROM SETTINGS AS T WHERE T.NAME = ?" +
                "   UNION ALL" +
                "   SELECT \"\", \"\", \"\") AS T";

        Cursor cursor = db.rawQuery(query, new String[]{NAME_COLUMN_DATABASE, NAME_COLUMN_SERVER, NAME_COLUMN_USERNAME});

        if (cursor.moveToFirst()) {
            model.setBase(cursor.getString(0));
            model.setServer(cursor.getString(1));
            model.setLogin(cursor.getString(2));

        }

//        db.execSQL("DELETE FROM SETTINGS");

        cursor.close();
        db.close();

    }

    public static AuthRepository getInstance() {
        if (instance == null)
            instance = new AuthRepository();

        return instance;
    }

    public void login(String username,
                      String password,
                      String database,
                      String server,
                      @Nullable String nameEvent,
                      @Nullable INotify callback) throws Exception {

        if (database.isEmpty()
                || server.isEmpty()
                || username.isEmpty()
                || password.isEmpty()) {
            String msg = "Не заполнены обязательные поля для авторизации";
            if (callback != null) {
                callback.NotifyResponse(nameEvent, msg, -1);
                return;
            }
        }

        model.setLogin(username);
        model.setPassword(password);
        model.setServer(server);
        model.setBase(database);

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString().replace('-', '_');

        objectNotify = callback;
        String urlBase = String.format(ModelContext.URLBase, server, database);
        HTTPClient.Builder builder = new HTTPClient.Builder(urlBase)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT")
                .addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                .addHeader("Cache-Control", "post-check=0, pre-check=0")
                .addHeader("Pragma", "no-cache")
                .authentication(username, password)
                .pathURL("mobile/tasks/getlist?uid=" + uuidAsString)
                .method(HTTPClient.METHOD_SEND.POST)
                .callback(this);

        String bodyString = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("getlisttypeworks", true);
            bodyString = jsonObject.toString();

            builder.body(bodyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HTTPClient client = builder.build();
        client.setNameEvent(nameEvent).sendAsync();
    }

    public String getUsername() {
        return model.getLogin();
    }

    public String getPassword() {
        return model.getPassword();
    }

    public String getDataBase() {
        return model.getBase();
    }

    public String getServer() {
        return model.getServer();
    }

    public boolean setDatabase(String database) {
        SQLiteDatabase db = dbHelper.getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put("VALUE", database);

        int id = db.update("SETTINGS", contentValues, "NAME=?", new String[]{NAME_COLUMN_DATABASE});
        db.close();
        return id > 0;
    }

    public boolean setServer(String server) {
        SQLiteDatabase db = dbHelper.getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put("VALUE", server);

        int id = db.update("SETTINGS", contentValues, "NAME=?", new String[]{NAME_COLUMN_SERVER});
        db.close();
        return id > 0;
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) throws Exception {
        if (eventString.equals(AuthViewModel.NAME_EVENT_LOGIN)) {
            int code = (int) params[1];
            if (code == 200) {
                String table = "SETTINGS";
                SQLiteDatabase db = dbHelper.getWritableDB();
                insertDB(db, table, NAME_COLUMN_DATABASE, model.getBase());
                insertDB(db, table, NAME_COLUMN_SERVER, model.getServer());
                insertDB(db, table, NAME_COLUMN_USERNAME, model.getLogin());

                db.close();

                ModelContext.URLBase = String.format(ModelContext.URLBase, model.getServer(), model.getBase());
            }

        }

        if (objectNotify != null)
            objectNotify.NotifyResponse(AuthViewModel.NAME_EVENT_LOGIN, params[0], params[1]);

    }

    public boolean insertDB(SQLiteDatabase db, String table, String nameColumn, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Value", value);
        contentValues.put("Name", nameColumn);
        int result = db.update(table, contentValues, "Name=?", new String[]{nameColumn});
        if (result == 0)
            result = (int) db.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        return result > 0;
    }
}
