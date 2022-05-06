package com.example.mychartviewlibrary.calendar.list;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.data.Schedule;
import com.example.mychartviewlibrary.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView content;
    TextView date;
    TextView id;
    TextView color;
    ImageView colorCircle;
//    ConstraintLayout item_layout;

    public ViewHolder(@NonNull View itemView, OnScheduleItemClickListener listener) {
        super(itemView);

        title = itemView.findViewById(R.id.item_title);
        content = itemView.findViewById(R.id.item_content);
        date = itemView.findViewById(R.id.item_date);
        id = itemView.findViewById(R.id.item_id);
        color = itemView.findViewById(R.id.item_color);
        colorCircle = itemView.findViewById(R.id.color_circle);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedule schedule = new Schedule(id.getText().toString(), date.getText().toString(), title.getText().toString(), content.getText().toString(), color.getText().toString());
                listener.onItemClick(schedule);
            }
        });
//        itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), DayActivity.class);
//            Log.i("kongyi123A", "id = " + id.getText().toString() + " / content = " + content.getText().toString());
//            Schedule schedule = new Schedule(id.getText().toString(), date.getText().toString(), title.getText().toString(), content.getText().toString(), color.getText().toString());
//            intent.putExtra("info", schedule);
//            v.getContext().startActivity(intent);
//        });
    }
}
