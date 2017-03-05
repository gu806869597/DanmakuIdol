package cn.toushi.danmakuidol.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.toushi.danmakuidol.R;

/**
 * 作者： jimhao
 * 创建于： 2017/3/4
 * 包名： cn.toushi.danmakuidol.adapter
 * 文档描述：首页显示视频数据的Adapter适配器
 */

public class FileDisplayAdapter extends RecyclerView.Adapter<FileDisplayAdapter.FileDisplayViewHolder> {

    public Context mContext ;
    public List<FileBeans> mDataList ;
    public LayoutInflater mInflater ;

    public FileDisplayAdapter(Context mContext , List<FileBeans> mList){
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext ;
        mDataList = mList ;
    }

    @Override
    public FileDisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_main_list_item,parent,false);
        return new FileDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileDisplayViewHolder holder, int position) {
        FileBeans bean = mDataList.get(position);
        String title = bean.getName();
        String time = bean.getTIME();
        Bitmap img = bean.getFileImg();
        holder.videoName.setText(title);
        holder.videoImg.setImageBitmap(img);
        holder.videoTime.setText("播放时长："+time);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class FileDisplayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView videoImg ;
        public TextView videoName ;
        public TextView videoTime ;
        public ImageButton videoPlayBtn ;
        public CardView videoLayout ;
        public FileDisplayViewHolder(View itemView) {
            super(itemView);
            videoImg = (ImageView) itemView.findViewById(R.id.video_img);
            videoName = (TextView) itemView.findViewById(R.id.video_name);
            videoTime = (TextView) itemView.findViewById(R.id.video_time);
            videoPlayBtn = (ImageButton) itemView.findViewById(R.id.video_play);
            videoLayout = (CardView) itemView.findViewById(R.id.video_layout);
            videoLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener!=null){
                itemClickListener.onItemClick(getPosition(),v);
            }
        }
    }


    public OnFileItemClickListener itemClickListener ;

    public void setItemClickListener(OnFileItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
