package com.example.cameratest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.net.Uri;
import android.os.Bundle;
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
import com.example.util.GlobalUtil;
import com.umeng.analytics.MobclickAgent;

public class FirstActivity extends Activity {

	NavigationBar nb;
	ImageView iv1;
	ImageView iv2;
	ImageView iv3;
	ImageView iv4;
	ImageView iv5;
	ImageView iv6;
	ImageView iv7;
	ImageView iv8;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	TextView tv4;
	TextView tv5;
	TextView tv6; 
	TextView tv7;
	TextView tv8;
	Map <Integer,Card> cardMap;
	DataBaseHelper myDbHelper ;
    String parent;
	int [] imageViews=new int[]{
			R.id.imageView1,R.id.imageView2,R.id.imageView3,R.id.imageView4,
			R.id.imageView5,R.id.imageView6,R.id.imageView7,R.id.imageView8
	};
//	int [] image_id={ 0,0,0,0,0,0,0,0 };
//	初始化  将各个节点均设置为  非目录节点 
//	String [] item_type={ Constants.TYPE_CARD,Constants.TYPE_CARD,Constants.TYPE_CARD,Constants.TYPE_CARD
//			,Constants.TYPE_CARD,Constants.TYPE_CARD,Constants.TYPE_CARD,Constants.TYPE_CARD};
	List<ImageView> ivList;
	List<TextView> tvList;
	
	SharedPreferences sp;
	boolean firstTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		init();
		initUI();
		initDir();
		initCards();
//		由于 已经在数据库方面做了改进  第一次启动的时候会 将APK中的DB文件覆盖用户的数据库  所以 暂时不用在这是否是第一次启动进行  数据的初始化操作
//		if(!firstTime){initData();} 
	}
	public void init(){
		sp=getSharedPreferences("xiaoyudi", 0);
		firstTime=sp.getBoolean("firstTime", true);
		Log.i("sjl", "是不是第一次启动"+firstTime);
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
//  应用第一次启动的时候  初始化 所有需要的路径文件夹
	public void initDir(){
		if(firstTime){  
		    File dir_YY=new File(Constants.dir_path_yy);
		    File dir_PIC=new File(Constants.dir_path_pic);
		    if(!dir_YY.exists()){
		    	dir_YY.mkdirs(); 
		    	Log.i("sjl", "正在初始化  YY 路径问题");
			}
		    if(!dir_PIC.exists()){
		    	dir_PIC.mkdirs(); 
		    	Log.i("sjl", "正在初始化 PIC 路径问题");
			}
		    Log.i("sjl", "路径初始化成功");
		}
	}
	public void initCards(){
//     应用第一次启动的时候  初始化数据库
//      if(firstTime){  
		try {
      	  String path_image=GlobalUtil.getExternalAbsolutePath(this)+"/"+"XIAOYUDI"+"/PIC/";
      	  String path_audio=GlobalUtil.getExternalAbsolutePath(this)+"/"+"XIAOYUDI"+"/YY/";
			  copyAssets(images,path_image);
			  copyAssets(audios,path_audio);
		} catch (IOException e) {
			e.printStackTrace();
		}
//      }
		myDbHelper =  DataBaseHelper.getDataBaseHelper(FirstActivity.this);
		cardMap=myDbHelper.getChildsByParent(parent);
		
//		begin 以下是 为了在编辑界面返回到主页面的时候  刷新出现内容不同步的 临时解决方案
		for(int i=1;i<ivList.size();i++){
			ivList.get(i).setImageResource(R.drawable.ic_add);
//			ivList.get(i).setBackgroundResource(R.drawable.ic_add);
		}
//		end by 2013 07 31
		Log.i("sjl", "parent id is :"+parent);
	 		if(cardMap!=null){
	 			Log.i("sjl", "cardlist.size is "+cardMap.size());
	 			
	 			Iterator it=cardMap.keySet().iterator();  
	 			while(it.hasNext()){
	 				Integer key=(Integer)it.next();
	 				Card cardItem=cardMap.get(key);
		 			int position=cardItem.getPosition();
//		 			begin  正在 修改因 显示手机
//		 			File picFile = new File(Constants.dir_path_pic, cardItem.getImage_filename());
//			    	Uri uri=Uri.fromFile(picFile);
//					ivList.get(position).setImageURI(uri);
					Log.i("sjl", "正在初始化页面的各个ITEM cardItem.getImage_filename():"+cardItem.getImage_filename());
					Bitmap mybitmap=GlobalUtil.preHandleImage(null,Constants.dir_path_pic+cardItem.getImage_filename());
					ivList.get(position).setImageBitmap(mybitmap);
//					mybitmap.recycle();
//					mybitmap=null;
//					end 已完成
					
			    	
					if(cardItem.getType().equals(Constants.TYPE_CATEGORY)){
						ivList.get(position).setBackgroundResource(R.drawable.ic_category);
					}else{
						ivList.get(position).setBackgroundResource(R.drawable.ic_card);
					}
//					picFile=null;
//					uri=null;
					tvList.get(position).setText(cardItem.getName());
	 			}
	 		}
          
          sp.edit().putBoolean("firstTime", false).commit();

	}
	String[] images=new String[]{"p1_11.jpg","p1_12.jpg","p1_21.jpg","p1_22.jpg","p1_23.jpg","p1_24.jpg","p1_2.jpg"};
	String[] audios=new String[]{"s1_11.mp3","s1_12.mp3","s1_21.mp3","s1_22.mp3","s1_23.mp3","s1_24.mp3"};
	
	public void copyAssets(String [] resources,String path) throws IOException{
		
		Log.i("sjl", "正在复制资源 声音和图片资源");
		Log.i("sjl", "resources.length:"+resources.length);
		for(int i=0;i<resources.length;i++){
			String outFileName = path+resources[i];
			File file=new File(outFileName);
			Log.i("sjl", "-----outFileName："+outFileName);
			if(!file.exists()){
				InputStream myInput = getAssets().open(resources[i]);
		    	Log.i("sjl", "-----复制图片："+outFileName);
		    	OutputStream myOutput = new FileOutputStream(outFileName);
		    	byte[] buffer = new byte[1024];
		    	int length;
		    	while ((length = myInput.read(buffer))>0){
		    		myOutput.write(buffer, 0, length);
		    	}
		    	myOutput.flush();
		    	myOutput.close();
		    	myInput.close();
		    	Log.i("sjl", "资源文件初始化完成");
			}
		}
	}
	
    Intent intent;
	public void setOnClickListener(){
		for(int i=0;i<ivList.size();i++){
			intent =new Intent();
			intent.putExtra("position", i);
			intent.setClass(FirstActivity.this, Edit2Activity.class);
			intent.putExtra("parent", parent);
			if(!isLauchPage&&i==0){
//				do nothing
			}else{
				ivList.get(i).setOnLongClickListener(new ImageViewLongClickListener(intent));
			}
			ivList.get(i).setOnClickListener(new ImageViewListener(i));
		}
	}
	
	public class ImageViewListener implements  OnClickListener{
		int position ;
		public ImageViewListener(int position){
			this.position=position;
		}
		@Override
		public void onClick(View v) {
//				判断是否为目录
			if(!isLauchPage&&position==0){
				finish();
			}else{
//				if(position<cardMap.size()){
					Card cardItem=cardMap.get(position);
//					此处避免了 一种情况   比如： cardMap的size是 3  但是点击的位置是2 这符合第一个判断  但是 实际上的这个位置是空的   
					if(cardItem!=null){
						if(Constants.TYPE_CATEGORY.equals(cardMap.get(position).getType())){
							Log.i("sjl", "正在进入下 一级界面");
							Intent intent=new Intent();
							intent.putExtra("isLauchPage", false);
							intent.putExtra("name", cardMap.get(position).getName());
							intent.putExtra("parent", cardMap.get(position).getId());
							intent.setClass(FirstActivity.this, FirstActivity.class);
							startActivity(intent);
						}else{
							Toast.makeText(FirstActivity.this, "长按替换", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(FirstActivity.this, "长按替换", Toast.LENGTH_SHORT).show();
					}
				}
//			}
		}
	}
	
	public class ImageViewLongClickListener implements  OnLongClickListener{
		Intent intent ;
		public ImageViewLongClickListener(Intent intent){
			this.intent=intent;
		}
		@Override
		public boolean onLongClick(View v) {
//			需要在这传给第二个界面  当前页面已经有的布局 就不要给用户机会重复选择了.. by sjl 2013 0726
			Log.i("sjl", "点击事件得到的position:"+intent.getIntExtra("position", 0));
			startActivityForResult(intent, 10);
			return false;
		}
	}
	@ Override  
    protected void onActivityResult ( int requestCode , int resultCode , Intent data ){  
		if(requestCode==10){
			if(resultCode==3){
				String name=data.getStringExtra("name");
				String image=data.getStringExtra("image");
				String audio=data.getStringExtra("audio");
				String _id=data.getStringExtra("_id");
				String type=data.getStringExtra("type");
				int position=data.getIntExtra("position", 0);
				Card card=new Card();
				card.setName(name);
				card.setAudio(audio);
				card.setId(_id);
				card.setImage(image);
				card.setPosition(position);
				card.setType(type);
//				用新选择的CARD替换原有的Card
				cardMap.put(position, card);
				String filename=myDbHelper.queryFilename(image);
//				begin 正在修改  选取图片引起的oom错误   by sjl 2013 07 31
//				File picFile = new File(Constants.dir_path_pic, filename);
//		    	Uri uri=Uri.fromFile(picFile);
//				ivList.get(position).setImageURI(uri);
				Bitmap mybitmap=GlobalUtil.preHandleImage(ivList.get(position),Constants.dir_path_pic+filename);
				mybitmap=GlobalUtil.small(mybitmap);
				Log.i("sjl", "缩放了...");
				ivList.get(position).setImageBitmap(mybitmap);
				mybitmap.recycle();
				mybitmap=null;
				
//				end
				Log.i("sjl", "正在渲染图片.."+position);
				if(type.equals(Constants.TYPE_CATEGORY)){
					ivList.get(position).setBackgroundResource(R.drawable.ic_category);
				}else{
					ivList.get(position).setBackgroundResource(R.drawable.ic_card);
				}
//				picFile=null;
//				uri=null;
				tvList.get(position).setText(name);
				Log.i("sjl", "长按添加  返回到原页面   正在渲染 图片cardname:"+name);
			}
		}
    }
	public void initNavigationBar(){
		nb.setTvTitle("小雨滴");
		nb.setBtnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new  Intent();
				intent.setClass(FirstActivity.this, EditActivity.class);
				startActivity(intent);
			}
		});
		nb.setBtnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder  builder=new Builder(FirstActivity.this);
				builder.setTitle("设置").setItems(new String[]{"卡片","目录"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent=new  Intent();
						intent.setClass(FirstActivity.this, MainActivity.class);
						switch(which){
						case 0:
							intent.putExtra("type", Constants.TYPE_CARD);
							break;
						case 1:
							intent.putExtra("type", Constants.TYPE_CATEGORY);
							break;
						default:
							break; 
						}
						startActivity(intent);
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

	
	public void initNavigationBar2(String name){
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
	
	public SQLiteDatabase getDB(){
	        db=openOrCreateDatabase("xiaoyudi.db", Context.MODE_PRIVATE, null);  
	        return db; 
	}
	
	
//	public void initData(){
//		myDbHelper =  DataBaseHelper.getDataBaseHelper(FirstActivity.this);
//		
//			Cursor c = db.rawQuery("SELECT * FROM card WHERE parent_id = ?", new String[]{parent+""});
//			if(c!=null){
//				while (c.moveToNext()) {
//					Log.i("sjl", "1");
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
//					Log.i("sjl", "初始化 页面  name=>" + cardname + ", picindex=>" + pic + ", yyindex=>" + yy+"type:"+type);
//				}  
//			}
//	}
	
	boolean isLauchPage;
	public void initUI(){
		
		ivList=new ArrayList<ImageView>();
		tvList=new ArrayList<TextView>();
		nb=(NavigationBar)findViewById(R.id.navigationBar_edit);
		iv1=(ImageView)findViewById(R.id.imageView1);
		iv2=(ImageView)findViewById(R.id.imageView2);
		iv3=(ImageView)findViewById(R.id.imageView3);
		iv4=(ImageView)findViewById(R.id.imageView4);
		iv5=(ImageView)findViewById(R.id.imageView5);
		iv6=(ImageView)findViewById(R.id.imageView6);
		iv7=(ImageView)findViewById(R.id.imageView7);
		iv8=(ImageView)findViewById(R.id.imageView8);
		ivList.add(iv1);
		ivList.add(iv2);
		ivList.add(iv3);
		ivList.add(iv4);
		ivList.add(iv5);
		ivList.add(iv6);
		ivList.add(iv7);
		ivList.add(iv8);
		initTextView();
		Intent intent=getIntent();
		isLauchPage=intent.getBooleanExtra("isLauchPage", true);
		if(isLauchPage){
			initNavigationBar();
//			初始化  首页父节点   应该动态从数据库读取  有待完善 
			parent="af35431e-cdea-4d66-b32f-57bf683a25ce";
		}else{
			iv1.setImageResource(R.drawable.ic_return);
			tv1.setText("返回");
			parent=intent.getStringExtra("parent");
			initNavigationBar2(intent.getStringExtra("name"));
		}
		
		setOnClickListener();
		
	}
	public void initTextView(){
		tv1=(TextView)findViewById(R.id.textView1);
		tv2=(TextView)findViewById(R.id.textView2);
		tv3=(TextView)findViewById(R.id.textView3);
		tv4=(TextView)findViewById(R.id.textView4);
		tv5=(TextView)findViewById(R.id.textView5);
		tv6=(TextView)findViewById(R.id.textView6);
		tv7=(TextView)findViewById(R.id.textView7);
		tv8=(TextView)findViewById(R.id.textView8);
		tvList.add(tv1);
		tvList.add(tv2);
		tvList.add(tv3);
		tvList.add(tv4);
		tvList.add(tv5);
		tvList.add(tv6);
		tvList.add(tv7);
		tvList.add(tv8);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first, menu);
		return true;
	}

}
