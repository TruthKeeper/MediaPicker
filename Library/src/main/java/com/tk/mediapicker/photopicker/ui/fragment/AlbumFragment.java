package com.tk.mediapicker.photopicker.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.tk.mediapicker.photopicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.photopicker.ui.adapter.AlbumAdapter;
import com.tk.mediapicker.photopicker.ui.adapter.AlbumAdapter.OnAlbumSelectListener;
import com.tk.mediapicker.photopicker.bean.AlbumBean;
import com.tk.mediapicker.photopicker.bean.AlbumFolderBean;
import com.tk.mediapicker.photopicker.callback.OnFolderListener;
import com.tk.mediapicker.photopicker.callback.OnLoadAlbumListener;
import com.tk.mediapicker.photopicker.widget.AlbumItemDecoration;
import com.tk.mediapicker.photopicker.utils.AlbumUtils;
import com.tk.mediapicker.photopicker.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TK on 2016/9/24.
 */

public class AlbumFragment extends Fragment implements OnLoadAlbumListener {


    private TextView photoTip;
    private RecyclerView recyclerview;

    private GridLayoutManager gridLayoutManager;
    private Context mContext;
    private AlbumAdapter albumAdapter;
    //recyclerview list
    private List<AlbumBean> albumList = new ArrayList<AlbumBean>();
    private Bundle bundle;
    private AlphaAnimation showAnim;
    private AlphaAnimation dismissAnim;
    private OnFolderListener onFolderListener;
    private OnAlbumSelectListener onAlbumSelectListener;
    private OnScrollListener onScrollListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        bundle = getArguments();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, null);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        photoTip = (TextView) view.findViewById(R.id.photo_tip);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showAnim = new AlphaAnimation(0f, 1f);
        showAnim.setDuration(1000);
        dismissAnim = new AlphaAnimation(1f, 0f);
        dismissAnim.setDuration(1000);
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerview.setLayoutManager(gridLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new AlbumItemDecoration(mContext, 3));
        albumAdapter = new AlbumAdapter(mContext,
                albumList,
                bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT),
                bundle.getBoolean(Constants.PhotoPickConstants.IS_SINGLE));
        albumAdapter.setOnAlbumSelectListener(onAlbumSelectListener);
        recyclerview.setAdapter(albumAdapter);
        onScrollListener = new OnScrollListener();
        recyclerview.addOnScrollListener(onScrollListener);
        //初始化数据源
        AlbumUtils.initAlbumData(getActivity(), this);
    }

    /**
     * load加载完毕，只调用一次
     *
     * @param albumList
     * @param albumFolderList
     */
    @Override
    public void onComplete(List<AlbumBean> albumList, List<AlbumFolderBean> albumFolderList) {
        this.albumList.clear();
        this.albumList.addAll(albumList);
        //防止界面闪烁，后续设置
        albumAdapter.setShowCamera(bundle.getBoolean(Constants.PhotoPickConstants.SHOW_CAMERA, false));
        albumAdapter.notifyDataSetChanged();
        if (onFolderListener != null) {
            onFolderListener.onFolderComplete(albumFolderList);
        }
    }

    @Override
    public void onDestroyView() {
        if (onScrollListener != null) {
            recyclerview.removeOnScrollListener(onScrollListener);
            onScrollListener = null;
        }
        super.onDestroyView();

    }

    /**
     * 得到选中的album集合
     *
     * @return
     */
    public List<AlbumBean> getSelectList() {
        return albumAdapter.getCheckList();
    }

    /**
     * 切换list集合
     *
     * @param albumList
     * @param firstIndex
     */
    public void setAlbumList(List<AlbumBean> albumList, boolean firstIndex) {
        this.albumList.clear();
        this.albumList.addAll(albumList);
        albumAdapter.setShowCamera(firstIndex && bundle.getBoolean(Constants.PhotoPickConstants.SHOW_CAMERA, false));
        albumAdapter.notifyDataSetChanged();
        recyclerview.scrollToPosition(0);
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        public OnScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //顶部时间条的显示
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && photoTip.getVisibility() == View.VISIBLE) {
                photoTip.setVisibility(View.GONE);
                photoTip.clearAnimation();
                photoTip.startAnimation(dismissAnim);
            } else if (photoTip.getVisibility() == View.GONE
                    && albumList.size() != 0) {
                photoTip.setVisibility(View.VISIBLE);
                photoTip.clearAnimation();
                photoTip.startAnimation(showAnim);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position = gridLayoutManager.findFirstVisibleItemPosition();
            if (albumList.size() != 0) {
                photoTip.setText(DateUtils.getDateStr(albumList.get(position).getDate()));
            }
        }
    }

    public List<AlbumBean> getAlbumList() {
        return albumList;
    }

    public void setCheckList(List<AlbumBean> checkList) {
        albumAdapter.setCheckList(checkList);
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public void setOnFolderListener(OnFolderListener onFolderListener) {
        this.onFolderListener = onFolderListener;
    }
}
