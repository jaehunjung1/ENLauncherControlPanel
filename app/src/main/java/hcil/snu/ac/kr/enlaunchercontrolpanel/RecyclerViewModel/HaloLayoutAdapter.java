package hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

public class HaloLayoutAdapter extends RecyclerView.Adapter<HaloLayoutAdapter.HaloLayoutViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<HaloLayoutModel> haloLayoutModelArrayList;

    public HaloLayoutAdapter(Context context, ArrayList<HaloLayoutModel> hlmArrayList) {
        layoutInflater = LayoutInflater.from(context);
        this.haloLayoutModelArrayList = hlmArrayList;
    }

    @NonNull
    @Override
    public HaloLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = layoutInflater.inflate(R.layout.recyler_item_controlpanel, viewGroup, false);
        HaloLayoutViewHolder viewHolder = new HaloLayoutViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HaloLayoutViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(this.haloLayoutModelArrayList.get(i).getDrawableId());
        viewHolder.textView.setText(this.haloLayoutModelArrayList.get(i).getLabel());
    }

    @Override
    public int getItemCount() {
        return this.haloLayoutModelArrayList.size();
    }

    class HaloLayoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public HaloLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recycler_item_imageview);
            textView = itemView.findViewById(R.id.recycler_item_textview);

        }
    }
}
