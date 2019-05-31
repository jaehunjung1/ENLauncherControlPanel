package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
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
    String visVar, notiProp = ""; // selected variable & property


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
                    showMappingDialog();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!notiProp.isEmpty())
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


        // if both continuous, need to create the continuous graph
        if (MappingContainer.isBothContinuous(visVar, notiProp)) {
            // TODO
        } else {
            setMappingContent(dialogLayout);
        }

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

    private void setMappingContent(ViewGroup dialogLayout) {
        final LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();


        ((FrameLayout)dialogLayout.findViewById(R.id.dialog_frame_layout)).addView(
                inflater.inflate(R.layout.layout_nominal_mapping, (ViewGroup)getParent(), false)
        );

        TextView visVarTextView = dialogLayout.findViewById(R.id.vis_var_text_view);
        TextView notiPropTextView = dialogLayout.findViewById(R.id.noti_property_text_view);
        visVarTextView.setText(visVar);
        notiPropTextView.setText(notiProp);

        LinearLayout visVarDialogList = dialogLayout.findViewById(R.id.vis_var_dialog_list);
        LinearLayout notiPropDialogList = dialogLayout.findViewById(R.id.noti_prop_dialog_list);

        // setting visVarListView
        if (visVar.equals("Motion")) {
            for (int i = 0; i < visVarDialogList.getChildCount(); i++) {
                final FrameLayout frame = (FrameLayout) visVarDialogList.getChildAt(i);
                frame.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_rectangle));

                // TODO for now, adding just dummy text view
                TextView tv = new TextView(getContext());
                tv.setText("Motion " + i);

                frame.addView(tv);
            }
        } else if (visVar.equals("Shape")) {
            for (int i = 0; i < visVarDialogList.getChildCount(); i++) {
                final FrameLayout frame = (FrameLayout) visVarDialogList.getChildAt(i);
                frame.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_rectangle));

                // TODO for now, addign just dummy text view
                TextView tv = new TextView(getContext());
                tv.setText("Shape " + i);

                frame.addView(tv);
            }
        } else {
            for (int i = 0; i < visVarDialogList.getChildCount(); i++) {
                final FrameLayout frame = (FrameLayout) visVarDialogList.getChildAt(i);

                // TODO color 반영 될 수 있도록 변경
                final int origColor = ContextCompat.getColor(getContext(), R.color.holo_blue_light);
                frame.setBackgroundColor(origColor);

                frame.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ColorPicker cp = new ColorPicker((Activity)getContext(),
                                Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                        );
                        cp.show();
                        cp.enableAutoClose();
                        cp.setCallback(new ColorPickerCallback() {
                            @Override
                            public void onColorChosen(int color) {
                                frame.setBackgroundColor(color);
                            }
                        });
                    }
                });
            }
        }

        // Setting Noti Property ListView
        if (notiProp.equals("Keyword")) {
            for (int i = 0; i < notiPropDialogList.getChildCount(); i++) {
                final FrameLayout frame = (FrameLayout) notiPropDialogList.getChildAt(i);
                frame.getLayoutParams().width = Utilities.dpToPx(getContext(), 170);

                LinearLayout keywordFrame = (LinearLayout)inflater.inflate(
                        R.layout.layout_keyword_mapping, (ViewGroup)getParent(), false
                );
                final FlowLayout flowLayout = keywordFrame.findViewById(R.id.mapping_keyword_flowLayout);
                final ImageButton addButton = keywordFrame.findViewById(R.id.mapping_keyword_add_button);
                addButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                                getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                        );
                        final EditText editText = new EditText(getContext());
                        mBuilder.setView(editText);
                        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addKeywordToFlowLayout(flowLayout, editText.getText().toString(), inflater);
                            }
                        });
                        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        final AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
                frame.addView(keywordFrame);
            }
        } else {
            ArrayList<String> intervals = new ArrayList<>();
            if (notiProp.equals("Interaction Stage")) {
                for (int i = 1; i <= 5; i++) {
                    intervals.add("Stage " + i);
                }
            } else {
                for (int i = 1; i <= 5; i++) {
                    intervals.add(String.format(Locale.getDefault(),
                            "%.1f ~ %.1f", (1f / 5) * (i - 1), (1f / 5) * i
                            ));
                }
            }
            final ArrayAdapter<String> spinnerAdapter = getArrayAdapter(intervals);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            for (int i = 0; i < notiPropDialogList.getChildCount(); i++) {
                final FrameLayout frame = (FrameLayout) notiPropDialogList.getChildAt(i);

                final Spinner spinner = new Spinner(getContext());

                spinner.setAdapter(spinnerAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String text = (String) adapterView.getItemAtPosition(i);
                        // TODO do something
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
                frame.addView(spinner);
            }
        }
    }

    private void addKeywordToFlowLayout(final FlowLayout flowLayout, String keyword, LayoutInflater inflater) {
        final Chip chipView = (Chip)inflater.inflate(R.layout.chip_view_layout, null);
        chipView.setChipText(keyword);
        flowLayout.addView(chipView);
        chipView.setOnCloseClickListener(new OnCloseClickListener() {
            @Override
            public void onCloseClick(View v) {
                chipView.setVisibility(View.GONE);
                ((ViewGroup)chipView.getParent()).removeView(chipView);
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
