package com.tk.mediapicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.bean.FileBean;
import com.tk.mediapicker.callback.OnRecyclerClickListener;
import com.tk.mediapicker.ui.adapter.FileListAdapter;
import com.tk.mediapicker.utils.DocumentUtils;
import com.tk.mediapicker.utils.FileUtils;
import com.tk.mediapicker.widget.FolderItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by TK on 2016/10/28.
 * 文件夹界面
 */

public class FileListActivity extends BaseActivity implements View.OnClickListener {
    private Bundle bundle;
    private ImageView back;
    private TextView title;
    private RecyclerView recyclerview;
    private FileListAdapter fileListAdapter;
    private List<FileBean> fileList = new ArrayList<FileBean>();
    //跳转记录position和path
    private LinkedList<Pair<Integer, String>> history = new LinkedList<Pair<Integer, String>>();
    private boolean hasSd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if (bundle.getBoolean(Constants.AS_SYSTEM, false)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, Constants.DEFAULT_REQUEST);
            return;
        }
        setContentView(R.layout.activity_file_list);
        initView();
        fileList.addAll(FileUtils.initFileDataList(this));
        hasSd = FileUtils.hasSd(this);
        fileListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DEFAULT_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Constants.RESULT_SINGLE, true);
            intent.putExtra(Constants.RESULT_DATA, DocumentUtils.getPath(this, data.getData()));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new FolderItemDecoration(this));
        fileListAdapter = new FileListAdapter(this, fileList);
        recyclerview.setAdapter(fileListAdapter);

        back.setOnClickListener(this);
        recyclerview.addOnItemTouchListener(new OnRecyclerClickListener(recyclerview) {
            @Override
            public void onClick(int position) {
                FileBean bean = fileList.get(position);
                if (bean.isFile()) {
                    //回调
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT_SINGLE, true);
                    intent.putExtra(Constants.RESULT_DATA, bean.getPath());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(new File(bean.getPath())), FileUtils.getMIMEType(bean.getName()));
//                    startActivity(intent);
                } else {
                    //跳入
                    String path = bean.getPath();
                    history.addLast(new Pair<>(position, new File(path).getParent()));
                    fileList.clear();
                    fileList.addAll(FileUtils.getDataList(new File(path)));
                    fileListAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //未到根目录
            if (history.size() > 0) {
                if (history.size() == 1 && hasSd) {
                    fileList.clear();
                    history.clear();
                    fileList.addAll(FileUtils.initFileDataList(this));
                    fileListAdapter.notifyDataSetChanged();
                    return true;
                }
                Pair<Integer, String> pair = history.removeLast();
                fileList.clear();
                fileList.addAll(FileUtils.getDataList(new File(pair.second)));
                fileListAdapter.notifyDataSetChanged();
                recyclerview.scrollToPosition(pair.first);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
