package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.AuraPreview;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.VisualParamContainer;
import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.PreviewParamModel;

public class ControlPanelActivity extends AppCompatActivity {
    public AuraPreview auraPreview;

    static final int enavNum = 6; // number of ENAVs in preview


    public int enavShape;
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
        * // TODO EAAV도 AuraPreview에서 define 할 수 있도록 변경
        * */
        ImageView testImageView = new ImageView(ControlPanelActivity.this);
        testImageView.setId(View.generateViewId());
        testImageView.setImageResource(R.drawable.kakaotalk_logo);

        auraPreview.setEAAV(testImageView);


        /* *
        * Initial Data List Attaching (Notification Data Attaching)
        * currently, data list is simple integer list
        * */
        ArrayList<Integer> enavDataList = new ArrayList<>();
        for (int i = 1; i < enavNum; i++) {
            enavDataList.add(i);
        }

        /* *
         * Initial Visual Param List Attaching (Notification Data Attaching)
         * currently, visual param list is simple integer list
         * 각 원소는 ENAV 각각의 visual parameters
         * */
        ArrayList<Integer> enavVisualParamList = new ArrayList<>();
        for (int i = 1; i < enavNum; i++) {
            enavVisualParamList.add(i);
        }

        VisualParamContainer visualParamContainer = new VisualParamContainer(
                -1, 0, ContextCompat.getColor(this, R.color.theme), enavVisualParamList
        );
        auraPreview.setENAVList(enavDataList, visualParamContainer);


        /* *
        * PreviewParamModel Initializing
        * */
        paramModel = ViewModelProviders.of(this).get(PreviewParamModel.class);
        paramModel.init(-1, 0, ContextCompat.getColor(this, R.color.theme));
        paramModel.getKNumLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer k) {
                auraPreview.changeKNum(k);
            }
        });
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
