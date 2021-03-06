package tonchev.yourplace;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tonchev.yourplace.modul.MyConnectionChecker;

import static tonchev.yourplace.ChoseActivity.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickFragment extends Fragment {


    public static final int MIN_RADIUS = 1000;
    private PieChart chart;
    private float[]yData ={10.5f,10.5f,10.5f,10.5f,10.5f};
    private String[]xData ={"BARS","ATMS","CASINOS","HOTELS","CLUBS"};
    private SeekBar seekBar;
    private TextView radius;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_pick, container, false);

        seekBar = (SeekBar) root.findViewById(R.id.seek_bar);
        radius = (TextView) root.findViewById(R.id.set_radius);
        setRadius = MIN_RADIUS;

        chart = (PieChart) root.findViewById(R.id.chart);
        chart.setRotationEnabled(false);
        chart.setHoleRadius(25f);
        chart.setTransparentCircleAlpha(10);
        chart.animateY(1000);
        chart.setClickable(true);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
               int x = (int) h.getX();

                switch (x){
                    case 0:
                        selection="bar";
                        break;
                    case 1:
                        selection="atm";
                        break;
                    case 2:
                        selection="casino";
                        break;
                    case 3:
                        selection="hotel";
                        break;
                    case 4:
                        selection="night_club";
                        break;
                }
                TabLayout.Tab tab =  nTabLayout.getTabAt(1);
                if(!MyConnectionChecker.haveNetworkConnection(getActivity())) {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    tab.select();

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        addDataSet();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setRadius = progress*200 + MIN_RADIUS;
                radius.setText(setRadius + "m" );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                radius.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radius.setVisibility(View.GONE);
            }
        });
        return root;
    }

    private void addDataSet() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        for (int i = 0; i<yData.length;i++){
            yEntrys.add(new PieEntry(yData[i],xData[i]));
        }
        for (int i = 1;i<xData.length; i++){
            xEntrys.add(xData[i]);
        }
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Pick Entry");
        // Distance between parts
        pieDataSet.setSliceSpace(2);
        // Text size
        pieDataSet.setValueTextColor(R.color.golden);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColor(R.color.blueTransperent);
        pieDataSet.setValueFormatter(new MyValueFormatter());

        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.setUsePercentValues(false);
        chart.invalidate();
    }
    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###"); // use no decimals
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            // write your logic here
            if(value < 100) return "";

            return mFormat.format(value);
        }
    }

}
