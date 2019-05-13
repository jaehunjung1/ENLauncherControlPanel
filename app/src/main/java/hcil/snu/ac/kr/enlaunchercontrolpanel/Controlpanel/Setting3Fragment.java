package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

public class Setting3Fragment extends Fragment {

    public Setting3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup parentLayout = (ViewGroup)inflater.inflate(R.layout.fragment_setting3, container, false);
        return parentLayout;
    }

}
