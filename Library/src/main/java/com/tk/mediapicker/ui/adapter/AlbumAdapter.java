package com.tk.mediapicker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.mediapicker.R;
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.widget.AlbumCheckView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TK on 2016/8/10.
 * 相册的Adapter
 */
public class AlbumAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int CAMERA_TYPE = 1000;
    public static final int ITEM_TYPE = 1001;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MediaBean> mList;
    private boolean single;
    private int limit;
    private boolean showCamera;
    private List<MediaBean> checkList;
    private OnAlbumSelectListener onAlbumSelectListener;
    private int size;


    public AlbumAdapter(Context mContext, List<MediaBean> mList, int limit, boolean isSingle) {
        this.mContext = mContext;
        this.mList = mList;
        this.limit = limit;
        this.single = isSingle;
        if (!isSingle) {
            checkList = new ArrayList<MediaBean>();
        }
        this.mInflater = LayoutInflater.from(mContext);
        size = mContext.getResources().getDisplayMetrics().widthPixels / 3;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CAMERA_TYPE) {
            return new CameraHolder(mInflater.inflate(R.layout.album_camera_layout, parent, false));
        }
        if (single) {
            return new SingleHolder(mInflater.inflate(R.layout.album_single_layout, parent, false));
        } else {
            return new MultiHolder(mInflater.inflate(R.layout.album_multi_layout, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? mList.size() + 1 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return CAMERA_TYPE;
        }
        return ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraHolder) {
            return;
        }
        MediaBean bean = showCamera ? mList.get(position - 1) : mList.get(position);
        if (single) {
            SingleHolder singleHolder = (SingleHolder) holder;
            singleHolder.videoFlag.setVisibility(bean.isVideo() ? View.VISIBLE : View.GONE);
            Glide.with(mContext)
                    .load(bean.getPath())
                    .asBitmap()
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(singleHolder.item);
        } else {
            MultiHolder multiHolder = (MultiHolder) holder;
            multiHolder.videoFlag.setVisibility(bean.isVideo() ? View.VISIBLE : View.GONE);
            Glide.with(mContext)
                    .load(bean.getPath())
                    .asBitmap()
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(multiHolder.item);
            if (checkList.contains(bean)) {
                multiHolder.cover.setVisibility(View.VISIBLE);
                multiHolder.indicator.setChecked(true);
            } else {
                multiHolder.cover.setVisibility(View.GONE);
                multiHolder.indicator.setChecked(false);
            }
        }
    }

    class CameraHolder extends ViewHolder {
        public CameraHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                itemView.setOnClickListener(v -> onAlbumSelectListener.onCamera());
            }
        }
    }

    class SingleHolder extends ViewHolder {
        ImageView item;
        ImageView videoFlag;

        public SingleHolder(View itemView) {
            super(itemView);
            item = (ImageView) itemView.findViewById(R.id.item);
            videoFlag = (ImageView) itemView.findViewById(R.id.video_flag);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                int p = showCamera ? getAdapterPosition() - 1 : getAdapterPosition();
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = showCamera ? getAdapterPosition() - 1 : getAdapterPosition();
                        onAlbumSelectListener.onAlbumClick(p);
                    }
                });
            }
        }
    }

    class MultiHolder extends ViewHolder {

        ImageView item;
        ImageView videoFlag;
        View cover;
        AlbumCheckView indicator;

        public MultiHolder(View itemView) {
            super(itemView);
            item = (ImageView) itemView.findViewById(R.id.item);
            videoFlag = (ImageView) itemView.findViewById(R.id.video_flag);
            cover = itemView.findViewById(R.id.cover);
            indicator = (AlbumCheckView) itemView.findViewById(R.id.indicator);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = showCamera ? getAdapterPosition() - 1 : getAdapterPosition();
                        onAlbumSelectListener.onAlbumClick(p);
                    }
                });
            }
            indicator.setOnClickListener(v -> {
                if (indicator.isChecked()) {
                    //反选
                    indicator.setChecked(false);
                    checkList.remove(mList.get(showCamera ? getAdapterPosition() - 1 : getAdapterPosition()));
                    cover.setVisibility(View.GONE);
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onSelect(checkList.size());
                    }
                } else {
                    if (checkList.size() < limit) {
                        //还可以选择
                        indicator.setChecked(true);
                        checkList.add(mList.get(showCamera ? getAdapterPosition() - 1 : getAdapterPosition()));
                        cover.setVisibility(View.VISIBLE);
                        if (onAlbumSelectListener != null) {
                            onAlbumSelectListener.onSelect(checkList.size());
                        }
                    } else {
                        //不可以选择了
                    }
                }
            });
        }
    }

    public void setCheckList(List<MediaBean> checkList) {
        this.checkList = checkList;
        notifyDataSetChanged();
    }

    /**
     * 取得选中的集合
     *
     * @return
     */
    public List<MediaBean> getCheckList() {
        return checkList;
    }

    /**
     * 切换时设置是否显示首格拍照
     *
     * @param showCamera
     */
    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public interface OnAlbumSelectListener {
        //点击拍照
        void onCamera();

        //点击Item回调
        void onAlbumClick(int position);

        //多选时回调总选择数
        void onSelect(int select);
    }
}
