package tonchev.yourplace;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickFragment extends Fragment {

    private PieChart chart;
    private float[]yData ={10.5f,10.5f,10.5f,10.5f,10.5f};
    private String[]xData ={"BARS","ATMS","CASINOS","HOTELS","CLUBS"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_pick, container, false);
        chart = (PieChart) root.findViewById(R.id.chart);
        chart.setRotationEnabled(false);
        chart.setHoleRadius(25f);
        chart.setTransparentCircleAlpha(10);
        chart.animateY(1000);


        //chart.setCenterText("");
        //chart.setCenterTextSize(10);
       // chart.setDrawEntryLabels(true);
        addDataSet();

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
        // distanciqta mejdu razrezikite
        pieDataSet.setSliceSpace(2);
        // Kolko da e golqm teksta vutre
        pieDataSet.setValueTextColor(R.color.golden);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColor(R.color.blue);
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
