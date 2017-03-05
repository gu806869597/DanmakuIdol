package cn.toushi.danmakuidol;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.toushi.danmakuidol.adapter.FileBeans;
import cn.toushi.danmakuidol.adapter.FileDisplayAdapter;
import cn.toushi.danmakuidol.adapter.OnFileListScrollListener;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_list)
    RecyclerView mainList;
    @InjectView(R.id.main_addButton)
    FloatingActionButton mainAddButton;
    @InjectView(R.id.main_refresh)
    SwipeRefreshLayout mainRefresh;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private FileDisplayAdapter adapter;

    private ArrayList<FileBeans> mDataList;

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        toolbar.setTitle("弹幕播放器O(∩_∩)O~");
        setSupportActionBar(toolbar);
        loadPhoneVideoData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mainAddButton.setOnClickListener(new AddVideoButtonListener());
        mainRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPhoneVideoData();
            }
        });
        adapter.setItemClickListener(new OnFileItemClickListener());
        mainList.setOnScrollListener(new OnFileItemScrollListener());
    }

    /**
     * FloatingActionButton点击事件
     */
    class AddVideoButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

    class OnFileItemScrollListener extends OnFileListScrollListener {

        @Override
        public void onHide() {
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            mainAddButton.animate().translationY(mainAddButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        }

        @Override
        public void onShow() {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            mainAddButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    /**
     * 列表点击事件
     */
    class OnFileItemClickListener implements cn.toushi.danmakuidol.adapter.OnFileItemClickListener{

        @Override
        public void onItemClick(int position, View view) {
            FileBeans beans = mDataList.get(position);
            String path = beans.getPath();
            String title = beans.getName();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,DanmakuActivity.class);
            intent.putExtra("FilePath", path);
            intent.putExtra("FileTitle",title);
            startActivity(intent);
        }
    }

    /**
     * 方法描述：扫描手机视频数据
     */
    public void loadPhoneVideoData() {
        mainRefresh.setRefreshing(true);
        mDataList = new ArrayList<FileBeans>();
        ContentResolver resolver = this.getContentResolver();
        String str[] = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION};
        Cursor cursor = MainActivity.this.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null,
                null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            long duration = cursor.getLong(3);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。

            String hms = formatter.format(duration);
            //封面
            Bitmap thumbnail = getVideoThumbnail(path);
            FileBeans bean = new FileBeans();
            bean.setId(id);
            bean.setName(name);
            bean.setPath(path);
            bean.setTIME(hms);
            bean.setFileImg(thumbnail);
            mDataList.add(bean);
        }

        initDataListForAdapter(mDataList);
    }

    /**
     * 方法描述：扫描数据库视频数据
     */
    public void loadDatabaseVideoData() {

    }

    public static final int FILE_RAD_REQUEST_CODE = 0;//权限请求码

    /**
     * 6.0系统需要重写此方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //如果用户同意所请求的权限
        if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //UPDATE_DIALOG_PERMISSION_REQUEST_CODE和FORCE_UPDATE_DIALOG_PERMISSION_REQUEST_CODE这两个常量是library中定义好的
            //所以在进行判断时,必须要结合这两个常量进行判断.
            //非强制更新对话框
            if (requestCode == FILE_RAD_REQUEST_CODE) {
                //进行读取操作
                loadPhoneVideoData();
            }
        } else {
            //用户不同意,提示用户,如下载失败,因为您拒绝了相关权限
            Toast.makeText(this, "缺少权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法说明：初始化Adapter适配器
     *
     * @param fileBeans 传递给实体类的文件实体参数
     */
    public void initDataListForAdapter(List<FileBeans> fileBeans) {
        mainRefresh.setRefreshing(false);
        adapter = new FileDisplayAdapter(this, fileBeans);
        mainList.setLayoutManager(new LinearLayoutManager(this));
        mainList.setItemAnimator(new DefaultItemAnimator());
        mainList.setHasFixedSize(false);
        mainList.setAdapter(adapter);
    }


    /**
     * 方法说明：
     *
     * @param filePath 文件地址
     * @return 视频缩略图
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
