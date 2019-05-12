package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.pchmn.materialchips.ChipView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

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

        final ChipView chipView = parentLayout.findViewById(R.id.chipView);
        chipView.setOnDeleteClicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipView.setVisibility(View.INVISIBLE);
                ((ViewGroup)chipView.getParent()).removeView(chipView);
            }
        });

        final TextInputLayout keywordInputLayout = parentLayout.findViewById(R.id.keyword_text_input);
        final TextInputEditText keywordEditText = parentLayout.findViewById(R.id.keyword_editText);
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(keywordEditText.getWindowToken(), 0);
                    keywordEditText.setText("");
                    keywordInputLayout.clearFocus();
                }
                return false;
            }
        });



        return parentLayout;
    }

}
