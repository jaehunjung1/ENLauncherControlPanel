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


public class Setting1Fragment extends Fragment {
    private PreviewParamModel paramModel;

    final private ArrayList<String> shapeArr =
            new ArrayList<>(Arrays.asList("Circle", "Square"));

    public Setting1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paramModel = ViewModelProviders.of(getActivity()).get(PreviewParamModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup parentLayout = (ViewGroup) inflater.inflate(R.layout.fragment_setting1, container, false);

        final TextView notiShape, notiColor, settingDone, settingCancel;
        notiShape = parentLayout.findViewById(R.id.notiShapeResult_TextView);
        notiShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                );
                LinearLayout dialogLayout = (LinearLayout)getLayoutInflater()
                        .inflate(R.layout.dialog_noti_shape, parentLayout, false);
                mBuilder.setView(dialogLayout);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                final LinearLayout shapeList = dialogLayout.findViewById(R.id.dialog_list);
                final String[] selectedShape = new String[1];
                for (int i = 0; i < shapeArr.size(); i++) {
                    final TextView tv = new TextView(getContext());
                    tv.setText(shapeArr.get(i));
                    LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(
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
                        paramModel.setEnavColorLiveData(color);
                    }
                });
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