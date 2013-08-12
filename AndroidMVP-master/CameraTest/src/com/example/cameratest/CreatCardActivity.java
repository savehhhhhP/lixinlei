package com.example.cameratest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.example.util.GlobalUtil;
import com.example.util.ListenerUtil;
import com.umeng.analytics.MobclickAgent;

public class CreatCardActivity extends Activity implements OnClickListener {
    private ImageView preview;
    private Button saveBtn;
    private Button recordBtn;
    private Button playBtn;
    private Button stopBtn;
    private Button clearBtn;
    private Spinner catogerySP;
    //  private OnClickListener imgViewListener;  
    private Bitmap myBitmap;
    private byte[] mContent;
    public boolean isRecording = false;
    String returnString;
    DataBaseHelper dataBaseHelper;                                                              //数据库
    private static final int REQUEST_ALBUM = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CALENDAR = 2;
    private static final int REQUEST_CAMERA_CROP = 3;
    private static final int REQUEST_ALBUM_CROP = 4;
    //  存放语音文件的路径
//  这用于标示  当前记录数有多少
    int yyItemIndex;
    int picItemIndex;
    //  暂时搁置 该字段的 UUID生成功能
    String card_id;

    SQLiteDatabase db;
    File file;
    MediaRecorder mMediaRecorder;
    NavigationBar nb;
    EditText cardnameET;
    LinearLayout ll;
    boolean imageflag = false;
    //    操作的类型   cardtype 1  catogerytype 0
    String cardType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseHelper = DataBaseHelper.getDataBaseHelper(CreatCardActivity.this);
        Intent intent = getIntent();
        cardType = intent.getStringExtra("type");                                       //lxl 获得类型
        Log.i("lxl", "cardType is " + cardType);
        setContentView(R.layout.activity_camera2);
        preview = (ImageView) findViewById(R.id.uploadIV);
        preview.setOnClickListener(this);
//        配置友盟发送数据的机制  在线同步策略
        MobclickAgent.updateOnlineConfig(this);
        initUI();
        initSettings();
    }

    public void initSettings() {
        SharedPreferences sp = getSharedPreferences("xiaoyudi", 0);
        yyItemIndex = sp.getInt("yyItemIndex", 0);
        picItemIndex = sp.getInt("picItemIndex", 0);
//    	card_id=sp.getInt("card_id", 0);
    }

    public void initNavigation() {
        nb = (NavigationBar) findViewById(R.id.navigationBar1);
        if (cardType.equals(Constants.TYPE_CARD)) {
            nb.setTvTitle("新卡片");
        } else {
            nb.setTvTitle("新目录");
        }

        nb.setBtnLeftBacground(R.drawable.ic_back);
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nb.setBtnRightVisble(false);
        nb.setBtnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatCardActivity.this, "功能开发中..", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initSpinner() {
        Cursor datasource = dataBaseHelper.getCardTypes();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(CreatCardActivity.this, R.layout.listitem, datasource, new String[]{"name"}, new int[]{R.id.listitem});
        catogerySP.setAdapter(adapter);
        catogerySP.setOnItemSelectedListener(new ListenerUtil.SpinnerSelectedListener(datasource));
    }

    public void initUI() {
        initNavigation();
        saveBtn = (Button) findViewById(R.id.saveBtn);
        recordBtn = (Button) findViewById(R.id.recordBtn);
        playBtn = (Button) findViewById(R.id.playBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        cardnameET = (EditText) findViewById(R.id.cardnameET);
        catogerySP = (Spinner) findViewById(R.id.spinner1);
        ll = (LinearLayout) findViewById(R.id.catogeryPart);
        Log.i("lxl", "cardType :" + cardType);
        if (Constants.TYPE_CARD.equals(cardType)) {
//    		Log.i("lxl", "初始化下拉框 ");
//    		暂时不增加 提供用户选择 卡片 所属目录的功能
            ll.setVisibility(View.GONE);
//    		initSpinner();
        } else {
            ll.setVisibility(View.GONE);
        }
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playBtn.setEnabled(false);
                MediaPlayer mp = MediaPlayer.create(CreatCardActivity.this, Uri.fromFile(file));
                if (mp != null) {
                    mp.start();
                    mp.setOnCompletionListener(new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                            playBtn.setEnabled(true);
                        }
                    });
                }
            }
        });
        stopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                playBtn.setEnabled(true);
                clearBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                playBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                clearBtn.setEnabled(false);
                Toast.makeText(CreatCardActivity.this, "已删除", Toast.LENGTH_LONG).show();
            }
        });
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageflag) {
                    if (isRecording) {
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    }
                    String name = cardnameET.getText().toString();
//					UUID 生成 
                    String image = GlobalUtil.getId();
                    String audio = GlobalUtil.getId();
                    card_id = GlobalUtil.getId();
                    Log.i("lxl", "生成的image audio的UUID分别是：" + image + "-" + audio);
                    Log.i("lxl", "生成的card_id  UUID是：" + card_id);
                    String image_filename = picItemIndex + ".jpg";
                    String audio_filename = yyItemIndex + ".mp3";
                    dataBaseHelper.addCards(card_id, cardType, name, image, audio, image_filename, audio_filename);
//					begin 利用友盟插件收集 新建目录或者卡片的名称
                    HashMap<String, String> info = new HashMap<String, String>();
                    info.put("name", name);
                    info.put("type", cardType);
                    MobclickAgent.onEvent(CreatCardActivity.this, "newevent", info);
                    Log.i("lxl", "数据已经传送友盟");
//					end 
                    SharedPreferences sp = getSharedPreferences("xiaoyudi", 0);
                    sp.edit().putInt("yyItemIndex", ++yyItemIndex).putInt("picItemIndex", ++picItemIndex).commit();
                    Toast.makeText(CreatCardActivity.this, "保存 成功", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CreatCardActivity.this, "请上传图片", Toast.LENGTH_LONG).show();
                }
            }
        });
        recordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;
                file = new File(Constants.dir_path_yy + yyItemIndex + ".mp3");
                Toast.makeText(getApplicationContext(), "录音中,请对话筒讲话..", Toast.LENGTH_LONG)
                        .show(); 
                     /* 创建录音文件，第一个参数是文件名前缀，第二个参数是后缀，第三个参数是SD路径 */
                try {
                     /* 实例化MediaRecorder对象 */
                    mMediaRecorder = new MediaRecorder();
					 /* 设置麦克风 */
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					 /* 设置输出文件的格式 */
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					 /* 设置音频文件的编码 */
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					 /* 设置输出文件的路径 */
                    file.createNewFile();
                    mMediaRecorder.setOutputFile(file.getAbsolutePath());
					 /* 准备 */
                    mMediaRecorder.prepare();
					 /* 开始 */
                    mMediaRecorder.start();
                    recordBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUEST_ALBUM) {                                  //选择照片事件返回
            if (data != null) {
                Uri originalUri = data.getData();
                if (originalUri != null) {
                    performCrop(originalUri, tempFileUri, REQUEST_ALBUM_CROP);
                }
                imageflag = true;
            }
        } else if (requestCode == REQUEST_CAMERA) {                         //照相事件返回
            try {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {                              //点击了OK，说明需要设置选择的照片
                    Log.i("lxl", "照相完成。");
                    performCrop(tempFileUri, tempFileUri, REQUEST_CAMERA_CROP);
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "裁剪错误", Toast.LENGTH_LONG);
            }
        } else if (requestCode == REQUEST_CAMERA_CROP) {                      //先做照相，之后裁剪，之后返回
            if (resultCode == RESULT_OK) {
                preview.setImageURI(Uri.fromFile(new File(Constants.dir_path_pic + imageName)));
                tempFileUri = null;
                imageflag = true;
            }
        } else if (requestCode == REQUEST_ALBUM_CROP) {                         //先做相册照片选择，之后裁剪，之后返回
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        //Uri originalUri = data.getData();
                        Log.i("_ALBUM_CROP", "data不为空");
                        preview.setImageURI(tempFileUri);
                        // 将图片内容解析成字节数组
                        mContent = readStream(resolver.openInputStream(Uri.parse(tempFileUri.toString())));
                        // 将字节数组转换为ImageView可调用的Bitmap对象
                        myBitmap = getPicFromBytes(mContent, null);
                        File f = new File(Constants.dir_path_pic + imageName);
                        Log.i("_ALBUM_CROP", Constants.dir_path_pic + imageName);
                        f.createNewFile();
                        FileOutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
                        myBitmap.recycle();
                        fOut.close();
                    } catch (Exception e) {
                        Toast.makeText(this, "设置裁剪后图片错误", Toast.LENGTH_LONG);
                    }
                }
            }
        } else if (requestCode == REQUEST_CALENDAR) {
            if (resultCode == RESULT_OK) {
//                happenDate.setCalendar(data.getIntExtra("year", 1900), data.getIntExtra("month", 0), data.getIntExtra("day", 1));  
            }
        }
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    //begin 2013.8.7 照相功能增加corp image操作 lxl
    String imageName;
    Uri tempFileUri;                      //拍照后照片的Uri

    /**
     * 裁剪图片
     */
    public void performCrop(Uri uri, Uri output, int request) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            Log.i("corp image", "载入裁剪");
            intent.setDataAndType(uri, "image/*");//设置要裁剪的图片
            Log.i("corp image", "载入图片");
            intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
            intent.putExtra("aspectX", 4);// 这两项为裁剪框的比例.
            intent.putExtra("aspectY", 5);// x:y=1:1
            intent.putExtra("output", output);//保存到ouotput
            intent.putExtra("outputFormat", "JPEG");// 返回格式
            startActivityForResult(intent, request);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.uploadIV: {
                final CharSequence[] items =
                        {"相册", "拍照"};
                AlertDialog dlg = new AlertDialog.Builder(CreatCardActivity.this).setTitle("选择图片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                imageName = picItemIndex + ".jpg";
                                if (item == 1) {                                        //拍照
//                                	以下  以指定路径的方式 保存图片 lxl 2013 07 23 
                                    File sdcardTempFile = new File(Constants.dir_path_pic, imageName);
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    tempFileUri = Uri.fromFile(sdcardTempFile);
                                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
                                    intent.putExtra("return-data", true);
                                    startActivityForResult(intent, REQUEST_CAMERA);
                                } else {                                               //相册选择
                                    File sdcardTempFile = new File(Constants.dir_path_pic, imageName);
                                    tempFileUri = Uri.fromFile(sdcardTempFile);
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                    getImage.setType("image/jpeg");
                                    startActivityForResult(getImage, REQUEST_ALBUM);
                                }
                            }
                        }).create();
                dlg.show();
            }
            break;
            default:
                break;
        }
    }

    //end------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}  