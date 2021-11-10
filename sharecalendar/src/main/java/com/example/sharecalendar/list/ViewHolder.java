package com.example.sharecalendar.list;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharecalendar.R;

public class ViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView content;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.item_title);
        content = itemView.findViewById(R.id.item_content);
    }
}
