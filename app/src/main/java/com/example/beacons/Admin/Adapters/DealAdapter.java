package com.example.beacons.Admin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beacons.Admin.Models.Deal;
import com.example.beacons.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter extends FirestoreRecyclerAdapter<Deal,DealAdapter.DealHolder>{



    public DealAdapter(@NonNull FirestoreRecyclerOptions<Deal> options) {
        super(options);
    }


    /**
     * The adapters allow us to show content using a recycler view.
     * This is to show a list of objects of a certain type. In this case we
     * are showing objects of the class Deal. This will allows us to show the users a list of their Deals.
     * the Deals can be deleted if the user does not need them anymore
     */

    class DealHolder extends RecyclerView.ViewHolder{
        TextView textViewDeal;
        TextView textViewPrice;
        TextView textViewTimes;
        TextView textViewUrl;


        public DealHolder(@NonNull View itemView) {

            super(itemView);
            textViewDeal=itemView.findViewById(R.id.deal_title);
            textViewPrice=itemView.findViewById(R.id.deal_price);
            textViewTimes=itemView.findViewById(R.id.text_view_times);
            textViewUrl=itemView.findViewById(R.id.deal_url);


        }
    }

    /**
     * The view holder will hold the values of the Object
     *   In this case we are working with a Deal, a price, a time and a URl
     * @param dealHolder
     * @param i
     * @param deal
     */
    @Override
    protected void onBindViewHolder(@NonNull DealHolder dealHolder, int i, @NonNull Deal deal) {

  dealHolder.textViewDeal.setText(deal.getDeal());
  dealHolder.textViewPrice.setText(deal.getPrice_Discount());
  dealHolder.textViewTimes.setText(deal.getTime());
  dealHolder.textViewUrl.setText(deal.getDeal_Link());

    }

    /**
     *The deal item passed a parameter bellow, contains all needed for a deal to be shown
     *It creates a deal per object
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal_item,
                parent,false);
        return new DealHolder(v);
    }

    /**
     * Deals get deleted from the database and the view
     * by swiping them
     * @param position
     */
    public void deleteDeal(int position){

        getSnapshots().getSnapshot(position)
                .getReference().delete();


    }
}
