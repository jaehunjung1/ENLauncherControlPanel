package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ResourceBundle;

import hcil.snu.ac.kr.enlaunchercontrolpanel.AuraPreview;
import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class ControlPanelActivity extends AppCompatActivity {
    private AuraPreview auraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlpanel);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new Setting1Fragment(), "FRAGMENT_SETTING1")
                .commit();

        auraPreview = findViewById(R.id.aura_preview);

        ImageView testImageView = new ImageView(ControlPanelActivity.this);
        testImageView.setId(View.generateViewId());
        testImageView.setImageResource(R.drawable.kakaotalk_logo);
        testImageView.setLayoutParams(new ConstraintLayout.LayoutParams(
                Utilities.dpToPx(ControlPanelActivity.this, 50),
                Utilities.dpToPx(ControlPanelActivity.this, 50)
        ));

        auraPreview.setEAAV(testImageView);
    }
}
