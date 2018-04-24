package com.example.mtaha.testbluetoothrasppi3;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Random;

/**
 * Created by mtaha on 2018-04-16.
 */

public class RandomDataGenerator extends AsyncTask<LineChart, Float, Float> {

    private final TextView measure;
    private final LineChart mChart;

    public RandomDataGenerator(LineChart mChart, TextView measure) {
        this.measure = measure;
        this.mChart = mChart;
    }

    @Override
    protected Float doInBackground(LineChart... params) {
        Random rm = new Random();

        while (true) {
            if (!isCancelled()) {
                try {
                    Thread.sleep(66);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress((new Float(rm.nextInt(100))));
                //addEntry(rm.nextFloat(), params[0]);
            }
            else{
               break;
            }
        }
        return new Float(0);
    }

    @Override
    protected void onCancelled() {
        //sauvegarder les donnes et les transmettre dans une base de donnee ou un fichier
    }

    @Override
    protected void onProgressUpdate(Float... stat) {
        addEntry(stat[0], mChart);
        measure.setText(stat[0]+"");
    }

    private void addEntry(Float stat, LineChart mChart) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), stat), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(100);
            mChart.setVisibleXRangeMaximum(100);

            // move to the latest entry
            //mChart.moveViewToX(data.getEntryCount());
            mChart.centerViewTo(data.getEntryCount(),65, YAxis.AxisDependency.LEFT);
            //mChart.setExtraRightOffset(650);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Memory Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        // To  show values of each point
        set.setDrawValues(true);
        //do not draw circles
        set.setDrawCircles(false);

        return set;
    }
}
