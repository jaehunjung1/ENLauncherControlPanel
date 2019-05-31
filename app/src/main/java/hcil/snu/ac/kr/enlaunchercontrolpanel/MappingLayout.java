package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.MappingContainer;


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

    ArrayAdapter<String> notiPropSpinnerAdapter;

    // Views
    private Spinner notiPropSpinner;

    // Data Parameters
    String visVar, notiProp; // selected variable & property


    public MappingLayout(Context context, String visVar) {
        super(context);
        inflate(getContext(), R.layout.mapping_layout, this);

        this.visVar = visVar;
        ((TextView)findViewById(R.id.visvar_textview)).setText(visVar);

        notiPropSpinnerAdapter = getArrayAdapter(MappingContainer.notiPropStringList);
        notiPropSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        notiPropSpinner = findViewById(R.id.notiProp_spinner);
        notiPropSpinner.setAdapter(notiPropSpinnerAdapter);

        // set item click listener for notiPropSpinner
        notiPropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    notiProp = "";
                } else {
                    notiProp = (String) adapterView.getItemAtPosition(i);
                }

                // TODO dialog function!!!
                showMappingDialog();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showMappingDialog();
            }
        });

    }

    public String getVisVar() {
        return this.visVar;
    }

    public String getNotiProp() {
        return this.notiProp;
    }

    private void showMappingDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
        );
        LinearLayout dialogLayout = (LinearLayout)((Activity)getContext()).getLayoutInflater()
                .inflate(R.layout.dialog_mapping, (ViewGroup)getParent(), false);
        mBuilder.setView(dialogLayout);
        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        // TODO set views for mapping dialog
        if ()

        FrameLayout frameLayout = dialogLayout.findViewById(R.id.dialog_frame_layout);



        View dialogDone = dialogLayout.findViewById(R.id.dialog_done);
        View dialogCancel = dialogLayout.findViewById(R.id.dialog_cancel);
        dialogDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO set Mapping Container
                mDialog.dismiss();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

    }


    private ArrayAdapter<String> getArrayAdapter(List<String> stringList) {
        return new ArrayAdapter<String>(getContext(), R.layout.spinner_item, stringList) {
            @Override
            public boolean isEnabled(int position){
                if (position == 0)
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
