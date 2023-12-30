package com.example.a75dayshardchallenge.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.day;

import java.util.List;

public class progressAdapter extends RecyclerView.Adapter<progressAdapter.ViewHolder> {
    List<day>dayList;

    public progressAdapter(List<day> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public progressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.progresss,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull progressAdapter.ViewHolder holder, int position) {

        String text=dayList.get(position).getId();
        boolean b1=dayList.get(position).isField1();
        boolean b2=dayList.get(position).isField2();
        boolean b3=dayList.get(position).isField3();
        boolean b4=dayList.get(position).isField4();
        boolean b5=dayList.get(position).isField5();
        boolean b6=dayList.get(position).isField6();

        holder.setData(text,b1,b2,b3,b4,b5,b6);


    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ProgressBar p1;
        ProgressBar p2;
        ProgressBar p3;
        ProgressBar p4;
        ProgressBar p5;
        ProgressBar p6;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.pdate);

            p1=itemView.findViewById(R.id.p1);
            p2=itemView.findViewById(R.id.p2);
            p3=itemView.findViewById(R.id.p3);
            p4=itemView.findViewById(R.id.p4);
            p5=itemView.findViewById(R.id.p5);
            p6=itemView.findViewById(R.id.p6);

        }

        public  void setData(String t,boolean b1,boolean b2,boolean b3,boolean b4,boolean b5,boolean b6)
        {
            textView.setText(t);
            if(b1)
            {
                p1.setProgress(100);
            }else
            {
                p1.setProgress(0);
            }
            if(b2)
            {
                p2.setProgress(100);
            }else
            {
                p2.setProgress(0);
            }
            if(b3)
            {
                p3.setProgress(100);
            }else
            {
                p3.setProgress(0);
            }
            if(b4)
            {
                p4.setProgress(100);
            }else
            {
                p4.setProgress(0);
            }
            if(b5)
            {
                p5.setProgress(100);
            }else
            {
                p5.setProgress(0);
            }

            if(b6)
            {
                p6.setProgress(100);
            }else
            {
                p6.setProgress(0);
            }
        }
    }
}
