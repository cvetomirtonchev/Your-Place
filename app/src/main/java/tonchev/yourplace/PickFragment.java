package tonchev.yourplace;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickFragment extends Fragment {

    private PieChart chart;
    private float[]yData ={10.5f,10.5f,10.5f,10.5f,10.5f};
    private String[]xData ={"BAR","CLUB","CASINO","ATM","HOTEL"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_pick, container, false);
        chart = (PieChart) root.findViewById(R.id.chart);
        chart.setRotationEnabled(false);
        chart.setHoleRadius(25f);
        chart.setTransparentCircleAlpha(10);
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
            yEntrys.add(new PieEntry(yData[i]));
        }
        for (int i = 1;i<xData.length; i++){
            xEntrys.add(xData[i]);
        }
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Pick Entry");
        // distanciqta mejdu razrezikite
        pieDataSet.setSliceSpace(2);
        // Kolko da e golqm teksta vutre
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColor(R.color.blue);
        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.invalidate();
    }

}
