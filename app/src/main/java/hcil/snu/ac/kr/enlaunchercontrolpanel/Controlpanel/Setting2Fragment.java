package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
import io.apptik.widget.MultiSlider;

public class Setting2Fragment extends Fragment {

    // Data Parameters
    int filterEnhancmentMin = 3;
    int filterEnhancmentMax = 7;
    int filterObservationWindowMin = 3;
    int filterObservationWindowMax = 7;
    ArrayList<String> filterKeywordArrayList = new ArrayList<>();



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
        final FlowLayout flowLayout = parentLayout.findViewById(R.id.flowLayout);
        final TextInputLayout keywordInputLayout = parentLayout.findViewById(R.id.keyword_text_input);
        final TextInputEditText keywordEditText = parentLayout.findViewById(R.id.keyword_editText);
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(keywordEditText.getWindowToken(), 0);
                    addKeyword(flowLayout, keywordEditText.getText().toString());
                    keywordEditText.setText("");
                    keywordInputLayout.clearFocus();
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
                        .replace(R.id.fragment, new Setting2Fragment(), "FRAGMENT_SETTING2")
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


    void addKeyword(FlowLayout flowLayout, String keyword) {
        filterKeywordArrayList.add(keyword);

        final Chip chipView = new Chip(getContext());
        FlowLayout.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                Utilities.dpToPx(getContext(), 27));
        chipView.setLayoutParams(lp);
        chipView.changeBackgroundColor(ContextCompat.getColor(getContext(), R.color.chip_background));
        chipView.setClosable(true);
        try {
            chipView.setChipIcon(ContextCompat.getDrawable(getContext(), R.drawable.cancel_icon));
            chipView.setCloseColor(ContextCompat.getColor(getContext(), R.color.white));
        } catch (Exception e) {
            e.printStackTrace();
        }
        chipView.setChipText(keyword);
//        chipView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        flowLayout.addView(chipView);
        chipView.setOnCloseClickListener(new OnCloseClickListener() {
            @Override
            public void onCloseClick(View v) {
                chipView.setVisibility(View.GONE);
                ((ViewGroup)chipView.getParent()).removeView(chipView);
                filterKeywordArrayList.remove(chipView.getChipText());
            }
        });
    }
}
