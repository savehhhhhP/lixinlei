package com.example.cameratest;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.po.Card;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.example.util.DataSyn;
import com.example.util.GlobalUtil;
import com.umeng.analytics.MobclickAgent;

public class FirstActivity extends Activity {
    public static final  String TAG = "FirstActivity";

    NavigationBar nb;                                            //导航条
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    ImageView iv4;
    ImageView iv5;
    ImageView iv6;

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    Map<Integer, Card> cardMap;
    DataBaseHelper myDbHelper;                                  //数据库
    String parent;

    MyHandler myHandler;

    List<ImageView> ivList;
    List<TextView> tvList;

    SharedPreferences sp;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        init();            //lxl系统初始化         对于是否第一次启动给出判断
        initUI();          //lxl界面初始化
        initDir();         //lxl应用第一次启动的时候  初始化 所有需要的路径文件夹      YY PIC
        initCards();
//		由于 已经在数据库方面做了改进  第一次启动的时候会 将APK中的DB文件覆盖用户的数据库  所以 暂时不用在这判断  是否是第一次启动进行数据的初始化操作
//		if(!firstTime){initData();} 
    }

    public void init() {
        myHandler = new MyHandler();
        sp = getSharedPreferences("xiaoyudi", 0);
        firstTime = sp.getBoolean("firstTime", true);
        Log.i(TAG, "是不是第一次启动" + firstTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCards();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void initDir() {
        if (firstTime) {
            File dir_YY = new File(Constants.dir_path_yy);
            File dir_PIC = new File(Constants.dir_path_pic);
            if (!dir_YY.exists()) {
                dir_YY.mkdirs();                                       //lxl 在存在的目录中创建文件夹  YY-声音
            }
            if (!dir_PIC.exists()) {
                dir_PIC.mkdirs();                                      //PIC 图片
            }
            Log.i(TAG, "路径初始化成功");
        }
    }

    String[] images ;
    String[] audios ;

    public void initCards() {
//     应用第一次启动的时候  初始化数据库
        images = this.getResources().getStringArray(R.array.images);
        audios = this.getResources().getStringArray(R.array.audios);
        try {
            String path_image = GlobalUtil.getExternalAbsolutePath(this) + "/" + "XIAOYUDI" + "/PIC/";
            String path_audio = GlobalUtil.getExternalAbsolutePath(this) + "/" + "XIAOYUDI" + "/YY/";
            copyAssets(images, path_image);  //复制资源 图片
            copyAssets(audios, path_audio);  //复制资源 声音
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDbHelper = DataBaseHelper.getDataBaseHelper(FirstActivity.this);           //lxl获取数据库
        cardMap = myDbHelper.getChildsByParent(parent);                                 //lxl根据父节点取得孩子节点内容

//		begin 以下是 为了在编辑界面返回到主页面的时候  刷新出现内容不同步的 临时解决方案
        for (int i = 1; i < ivList.size(); i++) {
            ivList.get(i).setImageResource(R.drawable.ic_add);
//			ivList.get(i).setBackgroundResource(R.drawable.ic_add);
        }
//		end by 2013 07 31
        Log.i(TAG, "parent id is :" + parent);
        if (cardMap != null) {
            Log.i(TAG, "cardlist.size is " + cardMap.size());

            Iterator it = cardMap.keySet().iterator();
            while (it.hasNext()) {
                Integer key = (Integer) it.next();
                Card cardItem = cardMap.get(key);
                int position = cardItem.getPosition();
                Log.i(TAG, "正在初始化页面的各个ITEM cardItem.getImage_filename():" + cardItem.getImage_filename());
                Bitmap mybitmap = GlobalUtil.preHandleImage(null, Constants.dir_path_pic + cardItem.getImage_filename());    //获得图片到 bitmap 之后放到imageViewList  ，循环结束后所有imageView都有图片
                ivList.get(position).setImageBitmap(mybitmap);

                if (cardItem.getType().equals(Constants.TYPE_CATEGORY)) {                            //lxl设置目录和一般card的相框样式
                    ivList.get(position).setBackgroundResource(R.drawable.ic_category);
                } else {
                    ivList.get(position).setBackgroundResource(R.drawable.ic_card);
                }
                tvList.get(position).setText(cardItem.getName());                                  //lxl设置文字
            }
        }
        sp.edit().putBoolean("firstTime", false).commit();
    }


    public void copyAssets(String[] resources, String path) throws IOException {

        Log.i(TAG, "正在复制资源 声音和图片资源");
        Log.i(TAG, "resources.length:" + resources.length);
        for (int i = 0; i < resources.length; i++) {
            String outFileName = path + resources[i];
            File file = new File(outFileName);
            Log.i(TAG, "-----outFileName：" + outFileName);
            if (!file.exists()) {
                InputStream myInput = getAssets().open(resources[i]);
                Log.i(TAG, "-----复制资源：" + outFileName);
                OutputStream myOutput = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
                Log.i(TAG, "资源文件初始化完成");
            }
        }
    }

    Intent intent;

    public void setOnClickListener() {                                //lxl设置不同图片的点击跳转到 同一个Edit2Activity但是传入不同的position值来区分入口，传入不同的parent来区分不同的父节点
        for (int i = 0; i < ivList.size(); i++) {
            intent = new Intent();
            intent.putExtra("position", i);
            intent.setClass(FirstActivity.this, CoverCardActivity.class);
            intent.putExtra("parent", parent);
            if (!isLauchPage && i == 0) {                                  //lxl 是目录则不做任何事，不是目录则添加长按响应
//				do nothing
            } else {
                ivList.get(i).setOnLongClickListener(new ImageViewLongClickListener(intent));
            }
            ivList.get(i).setOnClickListener(new ImageViewListener(i));    //lxl此处添加按键响应
        }
    }

    /**
     * 目录的图片的响应事件
     */
    public class ImageViewListener implements OnClickListener {
        int position;

        public ImageViewListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
//			判断是否为目录   如果为目录则进入
            if (!isLauchPage && position == 0) {                                                //position ==0？
                finish();
            } else {
                Card cardItem = cardMap.get(position);
//					此处避免了 一种情况   比如： cardMap的size是 3  但是点击的位置是2 这符合第一个判断  但是 实际上的这个位置是空的
                if (cardItem != null) {
                    if (Constants.TYPE_CATEGORY.equals(cardMap.get(position).getType())) {
                        Intent intent = new Intent();
                        intent.putExtra("isLauchPage", false);
                        intent.putExtra("name", cardMap.get(position).getName());
                        intent.putExtra("parent", cardMap.get(position).getId());
                        intent.setClass(FirstActivity.this, FirstActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FirstActivity.this, getString(R.string.FirstActivity_msg_cover), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FirstActivity.this, getString(R.string.FirstActivity_msg_cover), Toast.LENGTH_SHORT).show();
                }
            }
//			}
        }
    }

    public class ImageViewLongClickListener implements OnLongClickListener {
        Intent intent;

        public ImageViewLongClickListener(Intent intent) {
            this.intent = intent;
        }

        @Override
        public boolean onLongClick(View v) {
//			需要在这传给第二个界面  当前页面已经有的布局 就不要给用户机会重复选择了.. by sjl 2013 0726
            Log.i(TAG, "点击事件得到的position:" + intent.getIntExtra("position", 0));
            startActivityForResult(intent, 10);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == 3) {
                String name = data.getStringExtra("name");
                String image = data.getStringExtra("image");
                String audio = data.getStringExtra("audio");
                String _id = data.getStringExtra("_id");
                String type = data.getStringExtra("type");
                int position = data.getIntExtra("position", 0);
                Card card = new Card();
                card.setName(name);
                card.setAudio(audio);
                card.setId(_id);
                card.setImage(image);
                card.setPosition(position);
                card.setType(type);
//				用新选择的CARD替换原有的Card
                cardMap.put(position, card);
                String filename = myDbHelper.queryFilename(image);
//				begin 正在修改  选取图片引起的oom错误   by sjl 2013 07 31
                Bitmap mybitmap = GlobalUtil.preHandleImage(ivList.get(position), Constants.dir_path_pic + filename);
                mybitmap = GlobalUtil.small(mybitmap);                        //lxl对选择的图片进行缩放之后放到所选择的要替换的图片（）上
                Log.i(TAG, "缩放了...");
                ivList.get(position).setImageBitmap(mybitmap);
                mybitmap.recycle();
                mybitmap = null;

//				end
                Log.i(TAG, "正在渲染图片.." + position);
                if (type.equals(Constants.TYPE_CATEGORY)) {
                    ivList.get(position).setBackgroundResource(R.drawable.ic_category);
                } else {
                    ivList.get(position).setBackgroundResource(R.drawable.ic_card);
                }
//				picFile=null;
//				uri=null;
                tvList.get(position).setText(name);
                Log.i(TAG, "长按添加  返回到原页面   正在渲染 图片cardname:" + name);
            }
        }
    }

    public void initNavigationBar() {
        nb.setTvTitle("小雨滴");
        nb.setBtnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FirstActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new Builder(FirstActivity.this);
                builder.setTitle("设置").setItems(new String[]{"卡片", "目录","同步数据"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(FirstActivity.this, CreatCardActivity.class);
                        switch (which) {
                            case 0:
                                intent.putExtra("type", Constants.TYPE_CARD);
                                break;
                            case 1:
                                intent.putExtra("type", Constants.TYPE_CATEGORY);
                                break;
                            case 2:
                                //intent.putExtra("type",Constants.TYPE_SYN);
                                //begin此处实现同步功能 2013 8 13
                                DataSyn.getdb(myDbHelper);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            String synData;
                                            synData = DataSyn.getJsonString(Constants.GET_SQL);        //此处获得了SQL数据
                                            DataSyn.formatSql(synData);                                //更新数据表
                                            String mainifestData = DataSyn.getJsonString(Constants.GET_MAINIFEST); //获得mainfest数据
                                            Map<String,String> mapMain = DataSyn.getMainifest(mainifestData);     //解析mainfest数据
                                            int i=0;
                                            DataSyn.getResourceString(Constants.GET_FILE_PATH+mapMain.get("id_0"),myDbHelper.getFilename(mapMain.get("id_0")),Constants.dir_path_pic);
                                            DataSyn.getResourceString(Constants.GET_FILE_PATH+mapMain.get("id_1"),myDbHelper.getFilename(mapMain.get("id_1")),Constants.dir_path_yy);

                                            Message msg = new Message();
                                            Bundle b = new Bundle();//存放数据
                                            b.putString("msg", getString(R.string.FirstActivity_syn_msg_ok));
                                            msg.setData(b);
                                            FirstActivity.this.myHandler.sendMessage(msg);

                                        }catch(Exception e){
                                            Log.i("syn1",e.toString()  + "error");
                                            Message msg = new Message();
                                            Bundle b = new Bundle();//存放数据
                                            b.putString("msg", getString(R.string.FirstActivity_syn_msg_error));
                                            msg.setData(b);
                                            FirstActivity.this.myHandler.sendMessage(msg);
                                        }
                                    }
                                }).start();


//                                try{
//                                    DataSyn.goSyn(myDbHelper);
//                                }catch (Exception e){
//                                    Toast.makeText(FirstActivity.this,getString(R.string.FirstActivity_syn_msg),Toast.LENGTH_SHORT).show();
//                                }
                                return;
                                //end  8 16
                            default:
                                break;
                        }
                        if(which!=2){
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
    }

    public void initNavigationBar2(String name) {                                       //对导航栏的初始化——按钮的设置，背景图片，名称
        nb.setTvTitle(name);
        nb.setBtnLeftBacground(R.drawable.ic_back);
        nb.setBtnRightVisble(false);
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    SQLiteDatabase db;

    public SQLiteDatabase getDB() {
        db = openOrCreateDatabase("xiaoyudi.db", Context.MODE_PRIVATE, null);
        return db;
    }


//	public void initData(){
//		myDbHelper =  DataBaseHelper.getDataBaseHelper(FirstActivity.this);
//		
//			Cursor c = db.rawQuery("SELECT * FROM card WHERE parent_id = ?", new String[]{parent+""});
//			if(c!=null){
//				while (c.moveToNext()) {
//					Log.i(TAG, "1");
//					int id = c.getInt(c.getColumnIndex("_id"));  
//					String cardname = c.getString(c.getColumnIndex("cardname"));  
//					String pic= c.getString(c.getColumnIndex("pic"));  
//					String yy = c.getString(c.getColumnIndex("yy"));  
//					String type = c.getString(c.getColumnIndex("type"));  
//					int position= c.getInt(c.getColumnIndex("position"));  
//					image_id[position]=id;
//					if(type.equals(Constants.TYPE_CARD)){
//						item_type[position]=Constants.TYPE_CARD;
//					}else{
//						item_type[position]=Constants.TYPE_CATEGORY;
//					}
//					tvList.get(position).setText(cardname);
//					
////					再次判断 是否为装机时的自带图片
//					File picFile = new File(Constants.dir_path_pic, pic);
//			    	Uri uri=Uri.fromFile(picFile);
//					ivList.get(position).setImageURI(uri);
//					
//					picFile=null;
//					uri=null;
//					Log.i(TAG, "初始化 页面  name=>" + cardname + ", picindex=>" + pic + ", yyindex=>" + yy+"type:"+type);
//				}  
//			}
//	}

    boolean isLauchPage;

    public void initUI() {
        ivList = new ArrayList<ImageView>();
        tvList = new ArrayList<TextView>();
        nb = (NavigationBar) findViewById(R.id.navigationBar_edit);
        iv1 = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv3 = (ImageView) findViewById(R.id.imageView3);
        iv4 = (ImageView) findViewById(R.id.imageView4);
        iv5 = (ImageView) findViewById(R.id.imageView5);
        iv6 = (ImageView) findViewById(R.id.imageView6);
        ivList.add(iv1);
        ivList.add(iv2);
        ivList.add(iv3);
        ivList.add(iv4);
        ivList.add(iv5);
        ivList.add(iv6);
        initTextView();                                                //初始化textView   完成tvList的成员添加
        Intent intent = getIntent();
        isLauchPage = intent.getBooleanExtra("isLauchPage", true);       //取得是否为目录节点的信息，并且做出不同操作
        if (isLauchPage) {
            initNavigationBar();
//			初始化  首页父节点   应该动态从数据库读取  有待完善 
            parent = getString(R.string.firstParent);
        } else {
            iv1.setImageResource(R.drawable.ic_return);
            tv1.setText("返回");
            parent = intent.getStringExtra("parent");                    //取得父节点信息，
            initNavigationBar2(intent.getStringExtra("name"));
        }
        setOnClickListener();
    }

    public void initTextView() {
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv6 = (TextView) findViewById(R.id.textView6);
        tvList.add(tv1);
        tvList.add(tv2);
        tvList.add(tv3);
        tvList.add(tv4);
        tvList.add(tv5);
        tvList.add(tv6);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_first, menu);
        return true;
    }

    class MyHandler extends Handler {

        public MyHandler() {

        }

        public MyHandler(Looper L) {

            super(L);

        }

        //子类必须重写此方法,接受数据

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            //此处可以更新UI

            Bundle b = msg.getData();

            String error = b.getString("msg");
            Toast.makeText(FirstActivity.this,error,Toast.LENGTH_LONG).show();
        }

    }

}
