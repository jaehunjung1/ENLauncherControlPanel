package hcil.snu.ac.kr.enlaunchercontrolpanel.enaview;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;


public abstract class ENAView extends AppCompatImageView {

    public ENAView(Context context) {
        super(context);
    }

//    data attach 하는 부분이 여기 들어가야할 것 (모든 ENAView에 공통되는 부분이므로)
//    e.g. public abstract void setData();

}
