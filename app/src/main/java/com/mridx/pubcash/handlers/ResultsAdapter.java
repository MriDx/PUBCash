package com.mridx.pubcash.handlers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mridx.pubcash.R;
import com.mridx.pubcash.ResultActivity;

import java.util.ArrayList;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {

    ArrayList<Integer> matchid = new ArrayList<>();
    ArrayList<String> matchname = new ArrayList<>();
    ArrayList<String> matchtime = new ArrayList<>();
    ArrayList<String> mChicken = new ArrayList<>();
    ArrayList<String> mKill = new ArrayList<>();
    ArrayList<String> mFees = new ArrayList<>();

    public ResultsAdapter(ArrayList<Integer> matchid, ArrayList<String> matchname, ArrayList<String> matchtime, ArrayList<String> mChicken, ArrayList<String> mKill, ArrayList<String> mFees) {
        this.matchid = matchid;
        this.matchname = matchname;
        this.matchtime = matchtime;
        this.mChicken = mChicken;
        this.mKill = mKill;
        this.mFees = mFees;
    }

    @NonNull
    @Override
    public ResultsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_rows, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.MyViewHolder holder, int position) {
        holder.resultsMID.setText("Match ID " + matchid.get(position).toString());
        holder.resultsMName.setText(matchname.get(position).toString());
        holder.resultsMTime.setText("Match Held on " + matchtime.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return matchid.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView resultsMID, resultsMName, resultsMTime;
        public MyViewHolder(View view) {
            super(view);
            resultsMID = view.findViewById(R.id.resultsMID);
            resultsMName = view.findViewById(R.id.resultsMName);
            resultsMTime = view.findViewById(R.id.resultsMTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Results ", Toast.LENGTH_LONG).show();
                    //v.getContext().startActivity(new Intent(v.getContext(), ResultActivity.class));
                    Intent i = new Intent(v.getContext(), ResultActivity.class);
                    i.putExtra("resultsMID", matchid.get(getAdapterPosition()).toString());
                    i.putExtra("resultsMName", matchname.get(getAdapterPosition()));
                    i.putExtra("resultsMTime", matchtime.get(getAdapterPosition()));
                    i.putExtra("resultsMPrize", mChicken.get(getAdapterPosition()));
                    i.putExtra("resultsMKill", mKill.get(getAdapterPosition()));
                    i.putExtra("resultsMFees", mFees.get(getAdapterPosition()));
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
