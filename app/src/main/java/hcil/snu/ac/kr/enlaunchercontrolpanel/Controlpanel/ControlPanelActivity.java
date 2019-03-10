package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.AuraPreview;
import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.PreviewParamModel;

public class ControlPanelActivity extends AppCompatActivity {
    public AuraPreview auraPreview;


    public int enavShape; // 0: circle, 1: square
    public int enavColor;

    private PreviewParamModel paramModel;


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
        * Initial EAAV Attaching
        * */
        ImageView testImageView = new ImageView(ControlPanelActivity.this);
        testImageView.setId(View.generateViewId());
        testImageView.setImageResource(R.drawable.kakaotalk_logo);

        auraPreview.setEAAV(testImageView);

        /* *
        * Initial ENAV List Attaching
        * */
        ArrayList<ImageView> testENAVList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageView testENAV = new ImageView(ControlPanelActivity.this);
            testENAV.setId(View.generateViewId());
            Drawable enavDrawable = ContextCompat.getDrawable(
                    ControlPanelActivity.this, R.drawable.enav_circle_shape
            );
            enavDrawable.setColorFilter(new PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.theme), PorterDuff.Mode.MULTIPLY
            ));
            testENAV.setImageDrawable(enavDrawable);

            testENAVList.add(testENAV);
        }

        auraPreview.setENAVList(testENAVList);

        /* *
        * PreviewParamModel Initializing
        * */
        paramModel = ViewModelProviders.of(this).get(PreviewParamModel.class);
        paramModel.init(0, ContextCompat.getColor(this, R.color.theme));
        paramModel.getEnavShapeLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer shape) {
                enavShape = shape;
                auraPreview.changeENAVShapeAndColor(enavShape, enavColor);
            }
        });
        paramModel.getEnavColorLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer color) {
                enavColor = color;
                auraPreview.changeENAVShapeAndColor(enavShape, enavColor);
            }
        });

    }


}
