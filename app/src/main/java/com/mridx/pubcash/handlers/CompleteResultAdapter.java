package com.mridx.pubcash.handlers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mridx.pubcash.LoginActivity;
import com.mridx.pubcash.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import static com.mridx.pubcash.ResultActivity.perkill;
import static java.lang.Integer.parseInt;

public class CompleteResultAdapter extends RecyclerView.Adapter<CompleteResultAdapter.MyViewHolder> {

    ArrayList<Integer> sl1 = new ArrayList<>();
    ArrayList<String> k_no1 = new ArrayList<>();
    ArrayList<String> ingamename1 = new ArrayList<>();
    ArrayList<String> won1 = new ArrayList<>();

    public CompleteResultAdapter(ArrayList<Integer> sl1, ArrayList<String> k_no1, ArrayList<String> ingamename1, ArrayList<String> won1) {
        this.sl1 = sl1;
        this.k_no1 = k_no1;
        this.ingamename1 = ingamename1;
        this.won1 = won1;
    }


    @NonNull
    @Override
    public CompleteResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_rows, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CompleteResultAdapter.MyViewHolder holder, int position) {
        holder.crNo.setText(sl1.get(position).toString());
        holder.crName.setText(ingamename1.get(position));
        holder.crKill.setText(k_no1.get(position));
        //int won = parseInt(perkill) * parseInt(k_no1.get(position));
        holder.crWon.setText(won1.get(position));
    }

    @Override
    public int getItemCount() {
        return sl1.size();
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
