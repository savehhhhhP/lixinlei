package com.example.cameratest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.umeng.analytics.MobclickAgent;

public class EditCardActivity extends Activity implements OnClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_edit_card, menu);
        return true;
    }

    private ImageView preview;
    private Button saveBtn;
    private Button recordBtn;
    private Button playBtn;
    private Button stopBtn;
    private Button clearBtn;
    private TextView catogaryText;
    private Bitmap myBitmap;
    private byte[] mContent;
    private Spinner catogerySP;
    private boolean imageFlag = false;
    public String audio;
    public String image;
    public String audio_filename;
    public String image_filename;
    public String name;
    public String _id;
    public String type;
    File audioFile;
    File imageFile;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db = null;
    MediaRecorder mMediaRecorder;
    NavigationBar nb;
    EditText cardnameET;
    String returnString;


    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CALENDAR = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatcard);
        initUI();
        init();
//      配置友盟发送数据的机制  在线同步策略
        MobclickAgent.updateOnlineConfig(this);
        catogaryText.setVisibility(View.VISIBLE);
        catogerySP.setVisibility(View.GONE);
    }

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

    public void init() {
        dataBaseHelper = DataBaseHelper.getDataBaseHelper(EditCardActivity.this);
        Intent intent = getIntent();
        audio = intent.getStringExtra("audio");
        image = intent.getStringExtra("image");
        image_filename = dataBaseHelper.queryFilename(image);
        if (audio != null) {
            audio_filename = dataBaseHelper.queryFilename(audio);
        }
        name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        _id = intent.getStringExtra("_id");
        imageFile = new File(Constants.dir_path_pic, image_filename);
        preview = (ImageView) findViewById(R.id.uploadIV);
        if (imageFile != null) {
            Uri uri = Uri.fromFile(imageFile);
            preview.setImageURI(uri);
            uri = null;
            imageFile = null;
            imageFlag = true;
        } else {
            Log.i("sjl", "初始化过程中 检测到图片为NULL");
        }

        cardnameET.setText(name);
        catogaryText.setText("catogery");
        if (audio_filename != null) {
            audioFile = new File(Constants.dir_path_yy, audio_filename);
        }

        if (audioFile != null && audioFile.exists()) {
            playBtn.setEnabled(true);
        } else {
            playBtn.setEnabled(false);
        }
        recordBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        cardnameET.setEnabled(false);
        preview.setOnClickListener(this);
        preview.setEnabled(false);

    }

    //    -----------------------------------------------------------
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.uploadIV: {
                final CharSequence[] items =
                        {"相册", "拍照"};
                AlertDialog dlg = new AlertDialog.Builder(EditCardActivity.this).setTitle("选择图片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 1) {
                                    File sdcardTempFile = new File(Constants.dir_path_pic, image_filename);
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    Uri u = Uri.fromFile(sdcardTempFile);
                                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                                    intent.putExtra("return-data", true);
                                    startActivityForResult(intent, REQUEST_CAMERA);
                                } else {
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                    getImage.setType("image/jpeg");
                                    startActivityForResult(getImage, 0);
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

    public void initNavigation() {
        nb = (NavigationBar) findViewById(R.id.navigationBar1);
        nb.setBtnLeftBacground(R.drawable.ic_back);
        nb.setTvTitle("编辑卡片");
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nb.setBtnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                catogaryText.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                cardnameET.setEnabled(true);
                clearBtn.setEnabled(true);
                v.setEnabled(false);
                catogerySP.setVisibility(View.VISIBLE);
                preview.setEnabled(true);
            }
        });
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
        catogaryText = (TextView) findViewById(R.id.textView2);
        saveBtn.setVisibility(View.INVISIBLE);
        setupOnClickListener();
    }

    public boolean isRecording = false;

    public void setupOnClickListener() {
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFile.exists()) {
                    playBtn.setEnabled(false);
                    MediaPlayer mp = MediaPlayer.create(EditCardActivity.this, Uri.fromFile(audioFile));
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
                } else {
                    Toast.makeText(EditCardActivity.this, "没有声音文件", Toast.LENGTH_SHORT).show();
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
                if (audioFile.exists()) {
                    audioFile.delete();
                    audioFile = null;
                }
                playBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                clearBtn.setEnabled(false);
                Toast.makeText(EditCardActivity.this, "已删除", Toast.LENGTH_LONG).show();
            }
        });
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFlag) {
                    if (isRecording) {
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    }
                    String name = cardnameET.getText().toString();
                    dataBaseHelper.updateCardInfos(image_filename, audio_filename, name, _id, image, audio);
//					begin 利用友盟插件收集 新建目录或者卡片的名称
                    HashMap<String, String> info = new HashMap<String, String>();
                    info.put("name", name);
                    info.put("type", type);
                    MobclickAgent.onEvent(EditCardActivity.this, "addEvent", info);
                    Log.i("sjl", "保存成功  且数据已经传送友盟");
//					end 
                    finish();
                } else {
                    Toast.makeText(EditCardActivity.this, "请上传图片", Toast.LENGTH_SHORT).show();
                }
//			        维护应用的配置信息
            }
        });
        recordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;
                audioFile = new File(Constants.dir_path_yy + audio_filename);
                Toast.makeText(getApplicationContext(), "请对话筒讲话", Toast.LENGTH_LONG)
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
                    audioFile.createNewFile();
                    mMediaRecorder.setOutputFile(audioFile.getAbsolutePath());
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
        if (requestCode == 0) {
            try {
                if (data != null) {
                    Uri originalUri = data.getData();
                    if (originalUri != null) {
                        preview.setImageURI(originalUri);
//                       // 将图片内容解析成字节数组  
                        mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
//                       // 将字节数组转换为ImageView可调用的Bitmap对象  
                        myBitmap = getPicFromBytes(mContent, null);
                        File f = new File(Constants.dir_path_pic + image_filename);
                        f.createNewFile();
                        FileOutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
                        myBitmap.recycle();
                        originalUri = null;
                        f = null;
                        imageFlag = true;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else if (requestCode == REQUEST_CAMERA) {
            try {
//            	这是照片方法
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {
                    Log.i("sjl", "data is RESULT_OK");
                    String path = Constants.dir_path_pic + image_filename;
                    Uri u = Uri.fromFile(new File(path));
                    Log.i("sjl", "即将刷新");
                    preview.setImageURI(null);
                    preview.setImageURI(u);
                    imageFlag = true;
                    return;
                }
//                Bundle extras = data.getExtras(); 
//                myBitmap = (Bitmap) extras.get("data");  
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
//                mContent = baos.toByteArray();  
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 把得到的图片绑定在控件上显示  
//            preview.setImageBitmap(myBitmap);
//            preview.setImageBitmap(ImageUtil.toRoundCorner(myBitmap, 10));//把拍摄的照片转成圆角显示在预览控件上  
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


}
