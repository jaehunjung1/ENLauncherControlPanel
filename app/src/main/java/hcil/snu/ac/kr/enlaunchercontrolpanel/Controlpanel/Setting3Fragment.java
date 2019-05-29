package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel.HaloLayoutAdapter;
import hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel.HaloLayoutModel;

public class Setting3Fragment extends Fragment {

    private ArrayList<HaloLayoutModel> haloLayoutModelArrayList;
    int[] dummyDrawableIdList = {R.drawable.kakaotalk_logo, R.drawable.kakaotalk_logo,
            R.drawable.kakaotalk_logo, R.drawable.kakaotalk_logo};
    String[] dummyNameList = {"Preview 1", "Preview 2", "Preview 3", "Preview 4"};


    public Setting3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        haloLayoutModelArrayList = new ArrayList<>();
        updateHaloLayoutModelArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup parentLayout = (ViewGroup)inflater.inflate(R.layout.fragment_setting3,
                container, false);

        RecyclerView haloLayoutRecyclerView = parentLayout.findViewById(R.id.halo_layout_recyclerview);
        HaloLayoutAdapter haloLayoutAdapter = new HaloLayoutAdapter(getContext(), haloLayoutModelArrayList);
        haloLayoutRecyclerView.setAdapter(haloLayoutAdapter);
        haloLayoutRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        return parentLayout;
    }

    private void updateHaloLayoutModelArrayList() {
        for (int i = 0; i < dummyNameList.length; i++) {
            HaloLayoutModel model = new HaloLayoutModel();
            model.setDrawableId(dummyDrawableIdList[i]);
            model.setLabel(dummyNameList[i]);
            haloLayoutModelArrayList.add(model);
        }
    }

}
