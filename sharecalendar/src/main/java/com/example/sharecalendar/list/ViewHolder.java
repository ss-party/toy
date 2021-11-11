package com.example.sharecalendar.list;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharecalendar.R;
import com.example.sharecalendar.activity.DayActivity;
import com.example.sharecalendar.data.Schedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView content;
    TextView date;
    TextView id;
    TextView color;
//    ConstraintLayout item_layout;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.item_title);
        content = itemView.findViewById(R.id.item_content);
        date = itemView.findViewById(R.id.item_date);
        id = itemView.findViewById(R.id.item_id);
        color = itemView.findViewById(R.id.item_color);
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DayActivity.class);
            Schedule schedule = new Schedule(id.getText().toString(), date.getText().toString(), title.getText().toString(), content.getText().toString(), color.getText().toString());
            intent.putExtra("info", schedule);
            v.getContext().startActivity(intent);
        });
    }
}
