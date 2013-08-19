package com.example.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.po.Card;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.example.cameratest/databases/";
    private static String DB_NAME = "dropping.db";
    private static String myPath = DB_PATH + DB_NAME;
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static DataBaseHelper dataBaseHelper;


    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    private DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    //	danli
    public static DataBaseHelper getDataBaseHelper(Context context) {
        synchronized (DataBaseHelper.class) {
            if (dataBaseHelper == null) {
                dataBaseHelper = new DataBaseHelper(context);
                try {
                    dataBaseHelper.createDataBase();
                    dataBaseHelper.openDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataBaseHelper;
    }


    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
//		调试的时候将 此字段 设置为 false 实现每次启动都重新初始化 本测试机的数据库文件
//		dbExist=false;
        if (dbExist) {
            // do nothing - database already exist
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.i("dbTest", "检查数据库是否存在时异常。");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db               在指定路径，路径为创建数据库的位置
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream           开新数据库作为输出点
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile       从输入流转换byte到输出流 完成copy的过程
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * open the database
     */
    public void openDataBase() throws SQLException {
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        Log.i("dbTest", "数据库打开成功..");
    }


    public List<Card> getCards(String type_param) {
        List<Card> cardList = null;
        Card card;
        Cursor c;
        String sql = "SELECT * FROM card";

        if (type_param != null) {
            c = myDataBase.rawQuery(sql + " WHERE type = ?", new String[]{type_param});
        } else {
            c = myDataBase.rawQuery(sql, null);
        }

        if (c != null) {
            cardList = new ArrayList<Card>();
            while (c.moveToNext()) {
                card = new Card();
                int id = c.getInt(c.getColumnIndex("_id"));
                String name = c.getString(c.getColumnIndex("name"));
                String type = c.getString(c.getColumnIndex("type"));
                String image = c.getString(c.getColumnIndex("image"));
                String audio = c.getString(c.getColumnIndex("audio"));
                card.setName(name);
                card.setAudio(audio);
                card.setImage(image);
                card.setType(type);
                cardList.add(card);
                Log.i("dbTest", "card  name=>" + name + ", type=>" + type + ", image=>" + image + "audio:" + audio);
            }
        }
        return cardList;
    }

    /**
     * 删除操作
     *
     * @param id 标号
     */
    public void deleteCardById(String id) {
        myDataBase.delete("card", "id = ?", new String[]{id});
        myDataBase.delete("card_tree", "parent = ?", new String[]{id});
        myDataBase.delete("card_tree", "child = ?", new String[]{id});
        Log.i("dbTest", "删除操作已完成   删除记录的ID:" + id);
    }

    /**
     * 增加卡片操作
     *
     * @param id             标号
     * @param type           类型
     * @param name           名称
     * @param image          图片编号
     * @param audio          声音编号
     * @param image_filename 图片文件名
     * @param audio_filename 声音文件名
     */
    public void addCards(String id, String type, String name, String image, String audio, String image_filename, String audio_filename) {
        ContentValues cardValue = new ContentValues();
        cardValue.put("id", id);
        cardValue.put("type", type);
        cardValue.put("image", image);
        cardValue.put("audio", audio);
        cardValue.put("name", name);
        myDataBase.insert("card", null, cardValue);

        Log.i("dbTest", "插入完成card  name:" + name + "type " + type);

        ContentValues imageValue = new ContentValues();
        imageValue.put("filename", image_filename);
        imageValue.put("id", image);
        myDataBase.insert("resources", null, imageValue);
        Log.i("dbTest", "插入完成resources  image_filename:" + image_filename);

        ContentValues audioValue = new ContentValues();
        audioValue.put("filename", audio_filename);
        audioValue.put("id", audio);
        myDataBase.insert("resources", null, audioValue);
        Log.i("dbTest", "插入完成resources  audio_filename:" + audio_filename);
    }

    /**
     * 更新卡片信息
     *
     * @param image_filename 图片文件名
     * @param audio_filename 声音文件名
     * @param name           卡片名
     * @param id             卡片编号
     * @param image          图片编号
     * @param audio          声音编号
     */
    public void updateCardInfos(String image_filename, String audio_filename, String name, String id, String image, String audio) {

        Log.i("dbTest", "卡片编辑后台：image_filename" + image_filename);
        Log.i("dbTest", "卡片编辑后台：audio_filename" + audio_filename);
        Log.i("dbTest", "卡片编辑后台：name" + name);
        Log.i("dbTest", "卡片编辑后台：id" + id);
        Log.i("dbTest", "卡片编辑后台：image" + image);
        Log.i("dbTest", "卡片编辑后台：audio" + audio);

        if (image_filename != null) {
            ContentValues imageValue = new ContentValues();
            imageValue.put("filename", image_filename);
            myDataBase.update("resources", imageValue, "id = ?", new String[]{image});
            Log.i("dbTest", "正在更新 图片文件 名字 到数据库 ");
        }
        if (audio_filename != null) {
            ContentValues audioValue = new ContentValues();
            audioValue.put("filename", audio_filename);
            myDataBase.update("resources", audioValue, "id = ?", new String[]{audio});
            Log.i("dbTest", "正在更新 声音文件 名字  到数据库");
        }
        if (id != null && name != null) {
            ContentValues nameValue = new ContentValues();
            nameValue.put("name", name);
            myDataBase.update("card", nameValue, "id = ?", new String[]{id});
            Log.i("dbTest", "正在更新 卡片 名字  到数据库");
        }
    }

    public Cursor getDataSource(String type) {
//		the record named root_category is the root category    It would not be shown to user
//		To use this Cursor as the adapter for the listview  'id' column must be translated to '_id'
        Cursor c = myDataBase.rawQuery("SELECT id as _id,type,name,image,audio FROM card WHERE type = ? and name not in('root_category')", new String[]{type});
        return c;
    }

    public Cursor getCardTypes() {
        Cursor c = myDataBase.rawQuery("SELECT id as _id,name FROM card WHERE type = 'category' and name not in('root_category')", null);
        return c;
    }

    /**
     * 插入目录
     *
     * @param child    孩子节点
     * @param parent   父节点
     * @param position 位置
     */
    public void insertIntoCard_tree(String child, String parent, int position) {
        Log.i("dbTest", "begin to insert into card_tree..");
        String sql = "select * from card_tree where parent=? and position=?";
        Cursor response = myDataBase.rawQuery(sql, new String[]{parent, position + ""});
        if (response.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("child", child);
            long result = myDataBase.update("card_tree", values, "position=? and parent=? ", new String[]{position + "", parent});
            Log.i("dbTest", "update card_tree 语句  result :" + result);
        } else {
            ContentValues values = new ContentValues();
            values.put("child", child);
            values.put("parent", parent);
            values.put("position", position);
            Log.i("dbTest", "insert into card_tree with position:" + position + " parent:" + parent + " child" + child);
            long result = myDataBase.insert("card_tree", null, values);
            Log.i("dbTest", "after insert return value is long : " + result);
        }
    }

    public String queryFilename(String id) {
        String filename = null;
        Cursor c = myDataBase.rawQuery("SELECT filename FROM resources WHERE id = ? ", new String[]{id});
        if (c != null) {
            if (c.moveToFirst()) {
                filename = c.getString(c.getColumnIndex("filename"));
            }
        }
        return filename;
    }

    /**
     * 更新操作
     * @param sql 语句
     */
    public void updataTableData(String sql){
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);//打开数据库
        myDataBase.execSQL(sql);

    }

    /**
     * 查询操作
     */
    public String getFilename(String id){
        String fileNmae=null;
        String sql = "select filename from resources where id = ?";
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);//打开数据库
        Cursor response = myDataBase.rawQuery(sql,new String[]{id});
        if (response.moveToNext()) {
            fileNmae =  response.getString(response.getColumnIndex("filename"));
        }
        return fileNmae;
    }

    /**
     * 从父节点查出子节点信息
     *
     * @param parent 父节点信息
     * @return 从数据库中得到的childs数据
     * @throws SQLException
     */
    public Map<Integer, Card> getChildsByParent(String parent) throws SQLException {
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        Cursor response;
        Cursor image_resp;
        Cursor audio_resp;
        Map<Integer, Card> cardMap = null;
        Card card;
        if (myDataBase != null) {
            String sql = "select * from card_tree join card on card_tree.child = card.id where card_tree.parent = '"
                    + parent + "'";
            response = myDataBase.rawQuery(sql, null);
            Log.i("dbTest", sql);
            if (response != null) {
                cardMap = new HashMap<Integer, Card>();
                while (response.moveToNext()) {
                    String id = response.getString(response
                            .getColumnIndex("id"));
                    String name = response.getString(response
                            .getColumnIndex("name"));
                    String type = response.getString(response
                            .getColumnIndex("type"));
                    String image = response.getString(response
                            .getColumnIndex("image"));
                    String audio = response.getString(response
                            .getColumnIndex("audio"));
                    int position = response.getInt(response
                            .getColumnIndex("position"));
                    String image_filename = null;
                    String audio_filename = null;
                    if (image != null) {
                        image_resp = myDataBase.rawQuery(
                                "select filename from resources where resources.id='"
                                        + image + "'", null);
                        if (image_resp.moveToNext()) {
                            image_filename = image_resp.getString(image_resp
                                    .getColumnIndex("filename"));
                        }
                    }
                    if (audio != null) {
                        audio_resp = myDataBase.rawQuery(
                                "select filename from resources where resources.id='"
                                        + audio + "'", null);
                        if (audio_resp.moveToNext()) {
                            audio_filename = audio_resp.getString(audio_resp
                                    .getColumnIndex("filename"));
                        }
                    }
                    card = new Card();
                    card.setId(id);
                    card.setName(name);
                    card.setType(type);
                    card.setImage(image);
                    card.setAudio(audio);
                    card.setPosition(position);
                    card.setImage_filename(image_filename);
                    card.setAudio_filename(audio_filename);
                    cardMap.put(position, card);
                }
            }
        }

        return cardMap;
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}