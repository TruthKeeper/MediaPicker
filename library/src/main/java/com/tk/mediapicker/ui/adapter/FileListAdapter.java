package com.tk.mediapicker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk.mediapicker.R;
import com.tk.mediapicker.bean.FileBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/10/29.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ItemHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<FileBean> mList;
    private List<String> mCheckList;

    public FileListAdapter(Context mContext, List<FileBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mInflater = LayoutInflater.from(mContext);
        this.mCheckList = new ArrayList<String>();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mInflater.inflate(R.layout.file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.imageview.setImageResource(mList.get(position).isFile() ? R.drawable.vector_file : R.drawable.vector_file_folder);
        holder.textview.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView textview;

        public ItemHolder(View itemView) {
            super(itemView);
            imageview = (ImageView) itemView.findViewById(R.id.imageview);
            textview = (TextView) itemView.findViewById(R.id.textview);
        }
    }

}
