package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.Arrays;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.PreviewParamModel;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode;


public class Setting1Fragment extends Fragment {
    private PreviewParamModel paramModel;

    final private ArrayList<String> shapeArr = new ArrayList<>();
    final private ArrayList<String> paletteArr = new ArrayList<>();

    public Setting1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paramModel = ViewModelProviders.of(getActivity()).get(PreviewParamModel.class);

        // Initialize arrays of names for static visual params
        Utilities.getStaticResourceName(getContext(), shapeArr, R.array.shape_name);
        Utilities.getStaticResourceName(getContext(), paletteArr, R.array.palette_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup parentLayout = (ViewGroup) inflater.inflate(R.layout.fragment_setting1, container, false);

        final TextView aggregation, notiShape, notiColor, settingDone, settingCancel;
        aggregation = parentLayout.findViewById(R.id.aggregationResult_textview);
        aggregation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                );
                LinearLayout dialogLayout = (LinearLayout)getLayoutInflater()
                        .inflate(R.layout.dialog_setting_1, parentLayout, false);
                mBuilder.setView(dialogLayout);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                final TextView titleTextView = dialogLayout.findViewById(R.id.dialog_title_textview);
                titleTextView.setText("Choose K");
                final LinearLayout kNumList = dialogLayout.findViewById(R.id.dialog_list);
                final int[] selectedKNum = new int[1];
                final ArrayList<Integer> kNumArr = new ArrayList<>();
                kNumArr.add(-1);
                for (int i = 0; i < ControlPanelActivity.enavNum; i++) {
                    kNumArr.add(i);
                }
                for (int i = 0; i < kNumArr.size(); i++) {
                    final TextView tv = new TextView(getContext());
                    tv.setText(String.valueOf(kNumArr.get(i)));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, Utilities.dpToPx(getContext(), 45)
                    );
                    lp.weight = 1;
                    tv.setLayoutParams(lp);
                    tv.setPadding(Utilities.dpToPx(getContext(), 16), 0,
                            Utilities.dpToPx(getContext(), 16), 0);
                    tv.setTextSize(13);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setTag("unclicked");
                    kNumList.addView(tv);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < kNumList.getChildCount(); i++) {
                                TextView tempTv = (TextView)kNumList.getChildAt(i);
                                if (tempTv.getTag().toString().equals("clicked")) {
                                    tempTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                                    tempTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    tempTv.setTag("unclicked");
                                }
                            }
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
                            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.purple_check);
                            img.setBounds(0, 0, 50, 50);
                            tv.setCompoundDrawables(null, null, img, null);
                            tv.setTag("clicked");
                            selectedKNum[0] = kNumArr.get(kNumList.indexOfChild(tv));
                        }
                    });
                }
                View dialogDone = dialogLayout.findViewById(R.id.dialog_done);
                View dialogCancel = dialogLayout.findViewById(R.id.dialog_cancel);


                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paramModel.setKNumLiveData(selectedKNum[0]);
                        aggregation.setText(String.format("K = %s",
                                selectedKNum[0] > 0? selectedKNum[0] : "N" ));
                        mDialog.dismiss();
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });


            }
        });
        notiShape = parentLayout.findViewById(R.id.notiShapeResult_TextView);
        notiShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                );
                LinearLayout dialogLayout = (LinearLayout)getLayoutInflater()
                        .inflate(R.layout.dialog_setting_1, parentLayout, false);
                mBuilder.setView(dialogLayout);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                final LinearLayout shapeList = dialogLayout.findViewById(R.id.dialog_list);
                final String[] selectedShape = new String[1];
                for (int i = 0; i < shapeArr.size(); i++) {
                    final TextView tv = new TextView(getContext());
                    tv.setText(shapeArr.get(i));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, Utilities.dpToPx(getContext(), 45)
                            );
                    lp.weight = 1;
                    tv.setLayoutParams(lp);
                    tv.setPadding(Utilities.dpToPx(getContext(), 16), 0,
                            Utilities.dpToPx(getContext(), 16), 0);
                    tv.setTextSize(13);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setTag("unclicked");
                    shapeList.addView(tv);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < shapeList.getChildCount(); i++) {
                                TextView tempTv = (TextView)shapeList.getChildAt(i);
                                if (tempTv.getTag().toString().equals("clicked")) {
                                    tempTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                                    tempTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    tempTv.setTag("unclicked");
                                }
                            }
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
                            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.purple_check);
                            img.setBounds(0, 0, 50, 50);
                            tv.setCompoundDrawables(null, null, img, null);
                            tv.setTag("clicked");
                            selectedShape[0] = tv.getText().toString();
                        }
                    });

                    View dialogDone = dialogLayout.findViewById(R.id.dialog_done);
                    View dialogCancel = dialogLayout.findViewById(R.id.dialog_cancel);


                    dialogDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            paramModel.setEnavShapeLiveData(shapeArr.indexOf(selectedShape[0]));
                            notiShape.setText(selectedShape[0]);
                            mDialog.dismiss();
                        }
                    });

                    dialogCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });

                }

            }
        });

        notiColor = parentLayout.findViewById(R.id.notiColorResult_TextView);
        notiColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticMode mode = paramModel.getStaticModeLiveData().getValue();
                switch (mode) {
                    case SNAKE:
                    case PIZZA:
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                                getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                        );
                        LinearLayout dialogLayout = (LinearLayout)getLayoutInflater()
                                .inflate(R.layout.dialog_setting_1, parentLayout, false);
                        mBuilder.setView(dialogLayout);
                        final AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                        final TextView titleTextView = dialogLayout.findViewById(R.id.dialog_title_textview);
                        titleTextView.setText("Choose Color Palette");
                        final LinearLayout paletteList = dialogLayout.findViewById(R.id.dialog_list);
                        final String[] selectedPalette = new String[1];
                        for (int i = 0; i < paletteArr.size(); i++) {
                            final TextView tv = new TextView(getContext());
                            tv.setText(paletteArr.get(i));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, Utilities.dpToPx(getContext(), 45)
                            );
                            lp.weight = 1;
                            tv.setLayoutParams(lp);
                            tv.setPadding(Utilities.dpToPx(getContext(), 16), 0,
                                    Utilities.dpToPx(getContext(), 16), 0);
                            tv.setTextSize(13);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                            tv.setGravity(Gravity.CENTER_VERTICAL);
                            tv.setTag("unclicked");
                            paletteList.addView(tv);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < paletteList.getChildCount(); i++) {
                                        TextView tempTv = (TextView)paletteList.getChildAt(i);
                                        if (tempTv.getTag().toString().equals("clicked")) {
                                            tempTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                                            tempTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                            tempTv.setTag("unclicked");
                                        }
                                    }
                                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
                                    Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.purple_check);
                                    img.setBounds(0, 0, 50, 50);
                                    tv.setCompoundDrawables(null, null, img, null);
                                    tv.setTag("clicked");
                                    selectedPalette[0] = tv.getText().toString();
                                }
                            });

                            View dialogDone = dialogLayout.findViewById(R.id.dialog_done);
                            View dialogCancel = dialogLayout.findViewById(R.id.dialog_cancel);

                            dialogDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    paramModel.setEnavColorLiveData(selectedPalette[0]);
                                    notiColor.setText(selectedPalette[0]);
                                    notiColor.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                                    mDialog.dismiss();
                                }
                            });

                            dialogCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            });

                        }
                        break;
                    case PROGRESS:
                        int origColor = Integer.parseInt(notiColor.getTag().toString());
                        final ColorPicker cp = new ColorPicker(getActivity(),
                                Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                        );
                        cp.show();
                        cp.enableAutoClose();
                        cp.setCallback(new ColorPickerCallback() {
                            @Override
                            public void onColorChosen(int color) {
                                notiColor.setText(Utilities.colorHex(color));
                                notiColor.setTextColor(color);
                                notiColor.setTag(color);
                                paramModel.setEnavColorLiveData(String.valueOf(color));
                            }
                        });
                        break;
                }
            }
        });

        settingDone = parentLayout.findViewById(R.id.done_textview);
        settingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, new Setting2Fragment(), "FRAGMENT_SETTING2")
                        .commit();
            }
        });


        return parentLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
