package com.mridx.pubcash.handlers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mridx.pubcash.MatchDetailsActivity;
import com.mridx.pubcash.R;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder> {
    private ArrayList<String> partiCi = new ArrayList<>();
    private ArrayList<Integer> slno = new ArrayList<>();
    private Activity mActivity;

    public ParticipantsAdapter(MatchDetailsActivity activity, ArrayList<String> partiCi, ArrayList<Integer> slno) {
        this.mActivity = activity;
        this.partiCi = partiCi;
        this.slno = slno;
    }

    @Override
    public ParticipantsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participants, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParticipantsAdapter.MyViewHolder holder, int position) {
        //int i = 0;
        //int total = partiCi.size();
        holder.partView.setText(slno.get(position) + ". " + partiCi.get(position));
    }

    @Override
    public int getItemCount() {
        return partiCi.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView partView;
        public MyViewHolder(View view) {
            super(view);
            partView = view.findViewById(R.id.partView);
        }
    }
}
