package com.example.dhruv.twitteranalyser;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChart extends AppCompatActivity {

    TextView sr_term,number_tweets;
    public RelativeLayout mainLayout;
    public com.github.mikephil.charting.charts.PieChart mChart;

    public float[] yData = {AsyncTweet.postive_count, AsyncTweet.negative_count};
    public String[] xData = {"Positive", "Negative"};
    public CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Charting For " + MainActivity.searchterm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("TAG", String.valueOf(AsyncTweet.postive_count));
        Log.d("TAG", String.valueOf(AsyncTweet.negative_count));
        sr_term = (TextView) findViewById(R.id.sr_term);
       sr_term.setText(MainActivity.searchterm);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
        number_tweets = (TextView) findViewById(R.id.number);
        number_tweets.setText(String.valueOf(AsyncTweet.negative_count + AsyncTweet.postive_count));
        mainLayout = (RelativeLayout) findViewById(R.id.mLayouts);
        mChart = (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.mChart);
//        mainLayout.addView(mChart);
        //mainLayout.setBackgroundColor(new Color("#F3FFE1"));

        //configure pie chart
        mChart.setUsePercentValues(true);
        mChart.setDescription("Tweets Distribution");

        //enable hole and configure
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        //enable rotation of the chart by touch

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //set a chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                   @Override
                                                   public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                                                       //display message when value selected
                                                       if (e == null)
                                                           return;
                                                       Snackbar.make(coordinatorLayout, xData[e.getXIndex()] + " = " + e.getVal() + " Tweets", Snackbar.LENGTH_SHORT)
                                                               .setAction("Action", null).show();
                                                      // Toast.makeText(PieChart.this, xData[e.getXIndex()] + " = " + e.getVal() + " Tweets", Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onNothingSelected() {

                                                   }
                                               });

            //add Data
            addData();

            // customize legends
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);
        }

    public void addData()
    {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for(int i=0;i<yData.length;i++)
            yVals1.add(new Entry(yData[i],i) );

        ArrayList<String> xVals = new ArrayList<String>();

        for(int i=0;i<xData.length;i++)
            xVals.add(xData[i] );

        //create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1,"Sentiment");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c: ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for(int c: ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c: ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for(int c: ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object
        PieData data = new PieData(xVals,dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        //undo all highlights
        mChart.highlightValue(null);

        //update pie chart
        mChart.invalidate();


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
