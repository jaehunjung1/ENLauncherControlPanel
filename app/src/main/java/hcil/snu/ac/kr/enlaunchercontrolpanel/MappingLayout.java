package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.content.Context;
import android.widget.LinearLayout;

public class MappingLayout extends LinearLayout {
    final String[] visVarStringArr = {"Motion", "Position", "Shape", "Size", "Color"};
    final String[] notiPropStringArr = {"Importance", "Interaction Stage", "Keyword"};

    public MappingLayout(Context context) {
        super(context);

        inflate(getContext(), R.layout.mapping_layout, this);
    }





}
