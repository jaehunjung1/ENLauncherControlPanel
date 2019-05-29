package hcil.snu.ac.kr.enlaunchercontrolpanel.RecyclerViewModel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private Context context;

    public HaloLayoutAdapter(Context context, ArrayList<HaloLayoutModel> hlmArrayList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.haloLayoutModelArrayList = hlmArrayList;
    }

    @NonNull
    @Override
    public HaloLayoutAdapter.HaloLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = layoutInflater.inflate(R.layout.recyler_item_controlpanel, viewGroup, false);
        HaloLayoutViewHolder holder = new HaloLayoutViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView card = (CardView)((ViewGroup)view).getChildAt(0);
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chip_background));
            }
        });
        return holder;
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

        HaloLayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_item_imageview);
            textView = itemView.findViewById(R.id.recycler_item_textview);
        }
    }
}
