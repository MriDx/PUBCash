package com.mridx.pubcash.handlers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mridx.pubcash.R;
import com.mridx.pubcash.WalletActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import static com.mridx.pubcash.fragments.Transactions.parentTrans;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.MyViewHolder>{

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> transid = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> amount = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;

    public TransAdapter(ArrayList<String> date, ArrayList<String> transid, ArrayList<String> status, ArrayList<String> amount) {
        this.date = date;
        this.transid = transid;
        this.status = status;
        this.amount = amount;
    }

    @NonNull
    @Override
    public TransAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_rows, parent, false);
        return new TransAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransAdapter.MyViewHolder holder, int position) {
        //if (amount.size() > 0) {
            holder.transDate.setText(date.get(position));
            holder.transID.setText(transid.get(position));
            holder.status.setText(status.get(position));
            holder.amount.setText(amount.get(position));
        //} else {
           // Snackbar.make(parentTrans, "No Trasactions yet", Snackbar.LENGTH_LONG).show();
        //}

    }

    @Override
    public int getItemCount() {
        return amount.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView transDate, transID, status, amount;
        public MyViewHolder(View view) {
            super(view);
            transDate = view.findViewById(R.id.transDate);
            transID = view.findViewById(R.id.transID);
            status = view.findViewById(R.id.status);
            amount = view.findViewById(R.id.debit);
        }
    }

}
