package com.dmu.haeyagym;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {


    //private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;
    private TextView textView;
    public EventDecorator(Collection<CalendarDay> dates, Activity context, TextView textView) {
        //drawable = context.getResources().getDrawable(R.drawable.calendar_background);

        this.textView = textView;
        this.dates = new HashSet<>(dates);
    }

    public EventDecorator(int color, Collection<CalendarDay> dates) {

        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }

    public void setText(String text){
        textView.setText(text);
    }

}