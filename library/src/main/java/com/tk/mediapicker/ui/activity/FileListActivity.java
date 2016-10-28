package com.tk.mediapicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.utils.DocumentUtils;


/**
 * Created by TK on 2016/10/28.
 * 文件夹界面
 */

public class FileListActivity extends BaseActivity {
    private Bundle bundle;

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
}
