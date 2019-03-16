package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.AuraPreview;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.VisualParamContainer;
import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.PreviewParamModel;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode;

public class ControlPanelActivity extends AppCompatActivity {
    public AuraPreview auraPreview;

    static final int enavNum = 6; // number of ENAVs in preview


    public int enavShape;
    public String enavColor;

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
                StaticMode.SNAKE,-1, 0,
                "phaedra", enavVisualParamList
        );
        auraPreview.setENAVList(enavDataList, visualParamContainer);


        /* *
        * PreviewParamModel Initializing
        * */
        enavShape = 0;
        enavColor = "phaedra";
        paramModel = ViewModelProviders.of(this).get(PreviewParamModel.class);
        paramModel.init(StaticMode.SNAKE, -1, 0,
                "phaedra");

        paramModel.getStaticModeLiveData().observe(this, new Observer<StaticMode>() {
            @Override
            public void onChanged(@Nullable StaticMode staticMode) {
                auraPreview.changeStaticMode(staticMode);
            }
        });
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
        paramModel.getEnavColorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String color) {
                enavColor = color;
                auraPreview.changeENAVShapeAndColor(enavShape, enavColor);
            }
        });
    }
}
