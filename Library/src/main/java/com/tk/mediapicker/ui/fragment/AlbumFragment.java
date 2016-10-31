package com.tk.mediapicker.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseFragment;
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.bean.MediaFolderBean;
import com.tk.mediapicker.callback.OnFolderListener;
import com.tk.mediapicker.ui.adapter.AlbumAdapter;
import com.tk.mediapicker.ui.adapter.AlbumAdapter.OnAlbumSelectListener;
import com.tk.mediapicker.utils.AlbumUtils;
import com.tk.mediapicker.utils.DateUtils;
import com.tk.mediapicker.widget.AlbumItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by TK on 2016/9/24.
 */

public class AlbumFragment extends BaseFragment {


    private TextView photoTip;
    private RecyclerView recyclerview;

    private GridLayoutManager gridLayoutManager;
    private Context mContext;
    private AlbumAdapter albumAdapter;
    //recyclerview list
    private List<MediaBean> mediaList = new ArrayList<MediaBean>();
    private Bundle bundle;
    private AlphaAnimation showAnim;
    private AlphaAnimation dismissAnim;
    private OnFolderListener onFolderListener;
    private OnAlbumSelectListener onAlbumSelectListener;
    private OnScrollListener onScrollListener;

    private boolean hasInit;
    private boolean hasPermission;
    private Subscription subscription;
    private ProgressDialog dialog;

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
        photoTip = (TextView) view.findViewById(R.id.tip);
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
                mediaList,
                bundle.getInt(Constants.AlbumRequestConstants.CHECK_LIMIT,1),
                bundle.getBoolean(Constants.AlbumRequestConstants.AS_SINGLE,true));
        albumAdapter.setOnAlbumSelectListener(onAlbumSelectListener);
        recyclerview.setAdapter(albumAdapter);
        onScrollListener = new OnScrollListener();
        recyclerview.addOnScrollListener(onScrollListener);
        hasInit = true;
        initData();
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    /**
     * 初始化数据源
     */
    public void initData() {
        if (hasInit && hasPermission) {
            dialog = new ProgressDialog(mContext);
            subscription = Observable.fromCallable(() -> AlbumUtils.initData(mContext,
                    bundle.getBoolean(Constants.AlbumRequestConstants.SHOW_VIDEO, false)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<List<MediaBean>, List<MediaFolderBean>>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Pair<List<MediaBean>, List<MediaFolderBean>> listListPair) {
                            mediaList.clear();
                            mediaList.addAll(listListPair.first);
                            //防止界面闪烁，后续设置
                            albumAdapter.setShowCamera(bundle.getBoolean(Constants.AlbumRequestConstants.SHOW_CAMERA, false));
                            albumAdapter.notifyItemRangeInserted(0, mediaList.size());
                            if (onFolderListener != null) {
                                onFolderListener.onFolderComplete(listListPair.second);
                            }
                        }
                    });

        }

    }

    @Override
    public void onDestroyView() {
        if (onScrollListener != null) {
            recyclerview.removeOnScrollListener(onScrollListener);
            onScrollListener = null;
        }
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        super.onDestroyView();

    }

    /**
     * 得到选中的album集合
     *
     * @return
     */
    public List<MediaBean> getSelectList() {
        return albumAdapter.getCheckList();
    }

    /**
     * 切换list集合
     *
     * @param albumList
     * @param firstIndex
     */
    public void setAlbumList(List<MediaBean> albumList, boolean firstIndex) {
        this.mediaList.clear();
        this.mediaList.addAll(albumList);
        albumAdapter.setShowCamera(firstIndex && bundle.getBoolean(Constants.AlbumRequestConstants.SHOW_CAMERA, false));
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
                    && mediaList.size() != 0) {
                photoTip.setVisibility(View.VISIBLE);
                photoTip.clearAnimation();
                photoTip.startAnimation(showAnim);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position = gridLayoutManager.findFirstVisibleItemPosition();
            if (mediaList.size() != 0) {
                photoTip.setText(DateUtils.getDateStr(mediaList.get(position).getDate()));
            }
        }
    }

    public List<MediaBean> getMediaList() {
        return mediaList;
    }

    public void setCheckList(List<MediaBean> checkList) {
        albumAdapter.setCheckList(checkList);
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public void setOnFolderListener(OnFolderListener onFolderListener) {
        this.onFolderListener = onFolderListener;
    }
}
