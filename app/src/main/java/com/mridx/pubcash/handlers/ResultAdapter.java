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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import static com.mridx.pubcash.ResultActivity.perkill;
import static com.mridx.pubcash.ResultActivity.prize;
import static java.lang.Integer.parseInt;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    ArrayList<Integer> sl = new ArrayList<>();
    ArrayList<String> k_no = new ArrayList<>();
    ArrayList<String> ingamename = new ArrayList<>();
    ArrayList<String> won = new ArrayList<>();

    public ResultAdapter(ArrayList<Integer> sl, ArrayList<String> k_no, ArrayList<String> ingamename, ArrayList<String> won) {
        this.sl = sl;
        this.k_no = k_no;
        this.ingamename = ingamename;
        this.won = won;
    }

    @NonNull
    @Override
    public ResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_rows, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.MyViewHolder holder, int position) {
        holder.crNo.setText(sl.get(position).toString());
        holder.crName.setText(ingamename.get(position));
        holder.crKill.setText(k_no.get(position));
        //int won1 = parseInt(perkill) * parseInt(k_no.get(position));
        //int won = won1 + parseInt(prize);
        holder.crWon.setText(won.get(position));

    }

    @Override
    public int getItemCount() {
        return sl.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView crNo, crName, crKill, crWon;
        public MyViewHolder(View view) {
            super(view);
            /*
            resultsMID = view.findViewById(R.id.resultsMID);
            resultsMName = view.findViewById(R.id.resultsMName);
            resultsMTime = view.findViewById(R.id.resultsMTime);
            */
            crNo = view.findViewById(R.id.crNo);
            crName = view.findViewById(R.id.crName);
            crKill = view.findViewById(R.id.crKill);
            crWon = view.findViewById(R.id.crWon);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Results ", Toast.LENGTH_LONG).show();
                    //v.getContext().startActivity(new Intent(v.getContext(), ResultActivity.class));
                    Intent i = new Intent(v.getContext(), ResultActivity.class);
                    v.getContext().startActivity(i);
                }
            });*/
        }
    }

}
