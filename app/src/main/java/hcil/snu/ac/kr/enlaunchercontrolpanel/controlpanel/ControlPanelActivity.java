package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
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


        /* *
        * EAAV Attaching
        * */
        ImageView testImageView = new ImageView(ControlPanelActivity.this);
        testImageView.setId(View.generateViewId());
        testImageView.setImageResource(R.drawable.kakaotalk_logo);

        auraPreview.setEAAV(testImageView);

        /* *
        * List of ENAV Attcahing
        * */
        ArrayList<ImageView> testENAVList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageView testENAV = new ImageView(ControlPanelActivity.this);
            testENAV.setId(View.generateViewId());
            Drawable enavDrawable = ContextCompat.getDrawable(
                    ControlPanelActivity.this, R.drawable.enav_circle_shape
            );
            enavDrawable.setColorFilter(new PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY
            ));
            testENAV.setImageDrawable(enavDrawable);

            testENAVList.add(testENAV);
        }

        auraPreview.setENAVList(testENAVList);
    }
}
