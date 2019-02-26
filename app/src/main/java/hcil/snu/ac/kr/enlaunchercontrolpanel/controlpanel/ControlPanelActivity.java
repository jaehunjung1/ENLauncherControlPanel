package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

public class ControlPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlpanel);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new Setting1Fragment(), "FRAGMENT_SETTING1")
                .commit();

    }
}
