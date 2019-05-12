package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

import static android.support.v4.content.ContextCompat.getDrawable;

public class Setting2Fragment extends Fragment {

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
                    addKeywordChip(flowLayout, keywordEditText.getText().toString());
                    keywordEditText.setText("");
                    keywordInputLayout.clearFocus();
                }
                return false;
            }
        });




        return parentLayout;
    }


    void addKeywordChip(FlowLayout flowLayout, String keyword) {
        final Chip chipView = new Chip(getContext());
        FlowLayout.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                Utilities.dpToPx(getContext(), 27));
        chipView.setLayoutParams(lp);
        chipView.changeBackgroundColor(getResources().getColor(R.color.chip_background));
        chipView.setClosable(true);
        try {
            chipView.setChipIcon(ContextCompat.getDrawable(getContext(), R.drawable.cancel_icon));
            chipView.setCloseColor(getResources().getColor(R.color.white));
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
            }
        });
    }
}
