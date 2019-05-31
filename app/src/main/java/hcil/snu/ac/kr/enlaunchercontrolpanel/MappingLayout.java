package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * Mapping Information Container
 * 1) Each mapping layout configures exactly one mapping info container instance
 * - VisVar: Nominal ( motion, shape, color ) / Continuous ( position, size )
 * - NotiProp: Nominal ( interaction stage, keyword ) / Continuous ( importance )
 *
 * 2) i) VisVar: Nominal, NotiProp: Nominal: visVar is fixed, and noti prop has 5 frames
 *   with respective spinner
 *   ii) VisVar: Nominal, NotiProp: Continuous: visVar is same as above, and noti prop has 5 frames
 *   with respective spinner (each spinner has numerical interval as item )
 *   iii) VisVar: Continuous, NotiProp: Nominal: visVar is fixed as list of numerical intervals,
 *   and noti prop has 5 frames with respective spinner
 *   iv) VisVar: Continuous, NotiProp: Continuous: the only nasty case!!!
 *   (net-shaped graph, with start & end value respectively )
 *
 */

public class MappingLayout extends LinearLayout {
    final List<String> visVarStringList = new ArrayList<>(
            Arrays.asList("Visual Variable", "Motion", "Position", "Shape", "Size", "Color"));
    final List<String> notiPropStringList = new ArrayList<>(
            Arrays.asList("Noti Property", "Importance", "Interaction Stage", "Keyword"));
    ArrayAdapter<String> visVarSpinnerAdapter, notiPropSpinnerAdapter;


    // Views
    private Spinner visVarSpinner, notiPropSpinner;


    // Data Parameters
    String visVar, notiProp; // selected variable & property


    public MappingLayout(Context context) {
        super(context);
        inflate(getContext(), R.layout.mapping_layout, this);

        visVarSpinnerAdapter = getArrayAdapter(visVarStringList);
        visVarSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        notiPropSpinnerAdapter = getArrayAdapter(notiPropStringList);
        notiPropSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        visVarSpinner = findViewById(R.id.visvar_spinner);
        visVarSpinner.setAdapter(visVarSpinnerAdapter);
        notiPropSpinner = findViewById(R.id.notiProp_spinner);
        notiPropSpinner.setAdapter(notiPropSpinnerAdapter);

        // set item click listener for visVarSpinner
        visVarSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= 0) {
                    return;
                } else {
                    visVar = (String) adapterView.getItemAtPosition(i);
                }

                if (notiProp != null) {
                    // TODO: mappint ui 띄우는 helper function call
                }
            }
        });

        // set item click listener for notiPropSpinner
        notiPropSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= 0) {
                    return;
                } else {
                    notiProp = (String) adapterView.getItemAtPosition(i);
                }

                if (visVar != null) {

                }

            }
        });


    }

    public String getVisVar() {
        return this.visVar;
    }

    public String getNotiProp() {
        return this.notiProp;
    }


    private ArrayAdapter<String> getArrayAdapter(List<String> stringList) {
        return new ArrayAdapter<String>(getContext(), R.layout.spinner_item, stringList) {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
    }





}
