package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import io.apptik.widget.MultiSlider;

public class Setting2Fragment extends Fragment {

    // Data Parameters
    int filterEnhancmentMin = 3;
    int filterEnhancmentMax = 7;
    int filterObservationWindowMin = 3;
    int filterObservationWindowMax = 7;
    ArrayList<String> keywordBlackList = new ArrayList<>();
    ArrayList<String> keywordWhiteList = new ArrayList<>();



    public Setting2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup parentLayout = (ViewGroup) inflater.inflate(R.layout.fragment_setting2, container, false);

        // Enhancement Setting UI
        MultiSlider enhancementSeekbar = parentLayout.findViewById(R.id.enhancementSeekbar);
        enhancementSeekbar.getThumb(0).setValue(2);
        enhancementSeekbar.getThumb(1).setValue(7);
        enhancementSeekbar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    filterEnhancmentMin = value;
                } else {
                    filterEnhancmentMax = value;
                }
            }
        });

        // Observation Setting UI
        MultiSlider observationWindowSeekbar = parentLayout.findViewById(R.id.observationWindowSeekbar);
        observationWindowSeekbar.getThumb(0).setValue(2);
        observationWindowSeekbar.getThumb(1).setValue(7);
        observationWindowSeekbar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    filterObservationWindowMin = value;
                } else {
                    filterObservationWindowMax = value;
                }
            }
        });

        // Keyword Setting UI
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        final FlowLayout whiteFlowLayout = parentLayout.findViewById(R.id.whiteFlowLayout);
        final TextInputLayout whiteKeywordTextInput = parentLayout.findViewById(R.id.white_keyword_text_input);
        final TextInputEditText whiteKeywordEditText = parentLayout.findViewById(R.id.white_keyword_editText);
        whiteKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    imm.hideSoftInputFromWindow(whiteKeywordEditText.getWindowToken(), 0);
                    addKeyword(whiteFlowLayout, whiteKeywordEditText.getText().toString(), true);
                    whiteKeywordEditText.setText("");
                    whiteKeywordTextInput.clearFocus();
                }
                return false;
            }
        });

        final FlowLayout blackFlowLayout = parentLayout.findViewById(R.id.blackFlowLayout);
        final TextInputLayout blackKeywordTextInput = parentLayout.findViewById(R.id.black_keyword_text_input);
        final TextInputEditText blackKeywordEditText = parentLayout.findViewById(R.id.black_keyword_editText);
        blackKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(blackKeywordEditText.getWindowToken(), 0);
                    addKeyword(blackFlowLayout, blackKeywordEditText.getText().toString(), false);
                    blackKeywordEditText.setText("");
                    blackKeywordTextInput.clearFocus();
                }
                return false;
            }
        });

        TextView nextTextView = parentLayout.findViewById(R.id.next_textView);
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, new Setting3Fragment(), "FRAGMENT_SETTING3")
                        .addToBackStack(null)
                        .commit();
            }
        });

        TextView prevTextView = parentLayout.findViewById(R.id.prev_textView);
        prevTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    fm.beginTransaction()
                            .replace(R.id.fragment, new Setting1Fragment(), "FRAGMENT_SETTING1")
                            .commit();
                }
            }
        });

        return parentLayout;
    }


    void addKeyword(FlowLayout flowLayout, String keyword, boolean isWhite) {
        if (keyword.isEmpty()) return;
        final ArrayList<String> keywordList;
        final int backGroundColor;
        if (isWhite) {
            keywordList = keywordWhiteList;
            backGroundColor = ContextCompat.getColor(getContext(), R.color.holo_blue_light);
        } else {
            keywordList = keywordBlackList;
            backGroundColor = ContextCompat.getColor(getContext(), R.color.holo_red_light);
        }

        if (keywordList.size() == 0) {
            flowLayout.setVisibility(View.VISIBLE);
        }
        keywordList.add(keyword);


        final Chip chipView = (Chip)getLayoutInflater().inflate(R.layout.chip_view_layout, null);
        chipView.setChipText(keyword);
        chipView.changeBackgroundColor(backGroundColor);
        flowLayout.addView(chipView);
        chipView.setOnCloseClickListener(new OnCloseClickListener() {
            @Override
            public void onCloseClick(View v) {
                chipView.setVisibility(View.GONE);
                keywordList.remove(chipView.getChipText());
                if (keywordList.size() == 0) {
                    ((ViewGroup)chipView.getParent()).setVisibility(View.GONE);
                }
                ((ViewGroup)chipView.getParent()).removeView(chipView);

            }
        });
    }
}
