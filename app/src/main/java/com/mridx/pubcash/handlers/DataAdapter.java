package com.mridx.pubcash.handlers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mridx.pubcash.MatchDetailsActivity;
import com.mridx.pubcash.MatchViewActivity;
import com.mridx.pubcash.R;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    private ArrayList<Integer> mMatchId = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mChicken = new ArrayList<>();
    private ArrayList<String> mKill = new ArrayList<>();
    private ArrayList<String> mFees = new ArrayList<>();
    private ArrayList<String> mType = new ArrayList<>();
    private ArrayList<String> mMode = new ArrayList<>();
    private ArrayList<String> mMap = new ArrayList<>();
    private ArrayList<Integer> mTotal = new ArrayList<>();
    private Activity mActivity;

    private int lastposition = -1;
    public int mID;


    public DataAdapter(MatchViewActivity activity, ArrayList<Integer> mMatchId, ArrayList<String> mName, ArrayList<String> mTime, ArrayList<String> mChicken,
                       ArrayList<String> mKill, ArrayList<String> mFees,
                       ArrayList<String> mType, ArrayList<String> mMode,
                       ArrayList<String> mMap, ArrayList<Integer> mTotal) {
        this.mActivity = activity;
        this.mMatchId = mMatchId;
        this.mName = mName;
        this.mTime = mTime;
        this.mChicken = mChicken;
        this.mKill = mKill;
        this.mFees = mFees;
        this.mType = mType;
        this.mMode = mMode;
        this.mMap = mMap;
        this.mTotal = mTotal;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_rows, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.matchNo.setText(mMatchId.get(position).toString());
        holder.battleName.setText( mName.get(position));
        holder.timeView.setText("Match Time & Date : " + mTime.get(position));
        holder.prizeView.setText(mChicken.get(position));
        holder.killView.setText(mKill.get(position));
        holder.feesView.setText(mFees.get(position));
        holder.typeView.setText(mType.get(position));
        holder.modeView.setText(mMode.get(position));
        holder.mapView.setText(mMap.get(position));
        holder.progressDetailsFirst.setIndeterminate(false);
        holder.progressDetailsFirst.setMax(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.progressDetailsFirst.setMin(0);
        }
        holder.progressDetailsFirst.setProgress(mTotal.get(position));
        holder.joinedUsersFirst.setText(mTotal.get(position).toString() + "/100 Join");


    }

    @Override
    public int getItemCount() {
        return mMatchId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView matchNo, battleName, timeView, prizeView, killView, feesView, typeView, modeView, mapView, joinedUsersFirst;
        private ProgressBar progressDetailsFirst;

        public MyViewHolder(View view) {
            super(view);
            matchNo = (AppCompatTextView) view.findViewById(R.id.matchNo);
            battleName = (AppCompatTextView) view.findViewById(R.id.battleName);
            timeView = (AppCompatTextView) view.findViewById(R.id.timeView);
            prizeView = (AppCompatTextView) view.findViewById(R.id.prizeView);
            killView = (AppCompatTextView) view.findViewById(R.id.killView);
            feesView = (AppCompatTextView) view.findViewById(R.id.feesView);
            typeView = (AppCompatTextView) view.findViewById(R.id.typeView);
            modeView = (AppCompatTextView) view.findViewById(R.id.modeView);
            mapView = (AppCompatTextView) view.findViewById(R.id.mapView);
            progressDetailsFirst = view.findViewById(R.id.progressDetailsFirst);
            joinedUsersFirst = view.findViewById(R.id.joinedUsersFirst);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), MatchDetailsActivity.class);
                    i.putExtra("matchid" , mMatchId.get(getAdapterPosition()).toString());
                    i.putExtra("name", mName.get(getAdapterPosition()));
                    i.putExtra("time", mTime.get(getAdapterPosition()));
                    i.putExtra("chicken", mChicken.get(getAdapterPosition()));
                    i.putExtra("kill", mKill.get(getAdapterPosition()));
                    i.putExtra("fees", mFees.get(getAdapterPosition()));
                    i.putExtra("type", mType.get(getAdapterPosition()));
                    i.putExtra("mode", mMode.get(getAdapterPosition()));
                    i.putExtra("map", mMap.get(getAdapterPosition()));
                    v.getContext().startActivity(i);
                    //mActivity.overridePendingTransition(R.anim.from_left, R.anim.to_left);
                }
            });
        }
    }

}
