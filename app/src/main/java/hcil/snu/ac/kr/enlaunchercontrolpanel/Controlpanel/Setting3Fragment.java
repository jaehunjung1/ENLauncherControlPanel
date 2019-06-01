package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.alespero.expandablecardview.ExpandableCardView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.AggregatedMappingLayout;
import hcil.snu.ac.kr.enlaunchercontrolpanel.IndependentMappingLayout;
import hcil.snu.ac.kr.enlaunchercontrolpanel.R;
import hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel.HaloLayoutAdapter;
import hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel.HaloLayoutModel;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.MappingContainer;

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

        ExpandableCardView haloLayoutCardView = parentLayout.findViewById(R.id.halo_layout_card_view);
        RecyclerView haloLayoutRecyclerView = haloLayoutCardView.findViewById(R.id.halo_layout_recyclerview);
        HaloLayoutAdapter haloLayoutAdapter = new HaloLayoutAdapter(getContext(), haloLayoutModelArrayList);
        haloLayoutRecyclerView.setAdapter(haloLayoutAdapter);
        haloLayoutRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        ExpandableCardView indepVisCardView = parentLayout.findViewById(R.id.independent_vis_cardview);

        RecyclerView indepVisRecyclerView = indepVisCardView.findViewById(R.id.halo_layout_recyclerview);
        // TODO change this to independent vis adapter / indep vis ArrayList
        HaloLayoutAdapter indepVisAdapter = new HaloLayoutAdapter(getContext(), haloLayoutModelArrayList);
        indepVisRecyclerView.setAdapter(indepVisAdapter);
        indepVisRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        LinearLayout indepVisInnerView = indepVisCardView.findViewById(R.id.innerView);
        for (int i = 0; i < MappingContainer.visVarStringList.size(); i++) {
            indepVisInnerView.
                    addView(new IndependentMappingLayout(getContext(), MappingContainer.visVarStringList.get(i)));
        }


        ExpandableCardView aggregatedVisCardView = parentLayout.findViewById(R.id.aggregated_vis_cardview);
        RecyclerView aggregatedVisRecyclerview = aggregatedVisCardView.findViewById(R.id.halo_layout_recyclerview);
        // TODO change this to aggregated vis adatper / aggregated vis arraylist
        HaloLayoutAdapter aggregatedVisAdapter = new HaloLayoutAdapter(getContext(), haloLayoutModelArrayList);
        aggregatedVisRecyclerview.setAdapter(aggregatedVisAdapter);
        aggregatedVisRecyclerview.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        LinearLayout aggregateVisInnerView = aggregatedVisCardView.findViewById(R.id.innerView);
        for (int i = 0; i < MappingContainer.visVarStringList.size(); i++) {
            aggregateVisInnerView
                    .addView(new AggregatedMappingLayout(getContext(), MappingContainer.visVarStringList.get(i)));
        }


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