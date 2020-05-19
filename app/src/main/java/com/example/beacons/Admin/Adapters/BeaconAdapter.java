package com.example.beacons.Admin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beacons.Admin.Models.Beacon;
import com.example.beacons.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BeaconAdapter extends FirestoreRecyclerAdapter<Beacon,BeaconAdapter.BeaconHolder>{




    public BeaconAdapter(@NonNull FirestoreRecyclerOptions<Beacon> options) {
        super(options);
    }


    /**
     * The adapters allow us to show content using a recycler view.
     * This is to show a list of objects of a certain type. In this case we
     * are showing objects of the class Beacon. This will allows us to show the users a list of their beacons.
     * the beacons can be deleted if the user does not need them anymore
     */
    class BeaconHolder extends RecyclerView.ViewHolder{

        TextView textViewBeacon;
        TextView textViewcompaId;


        public BeaconHolder(@NonNull View itemView) {
            super(itemView);
            textViewBeacon= itemView.findViewById(R.id.beacon_id);
            textViewcompaId=itemView.findViewById(R.id.comp_id);


        }
    }

    /**
     * The view holder will hold the values of the Object
     *      * In this case we are working with a beaconId and a CompanyId
     * @param beaconHolder
     * @param i
     * @param beacon
     */
    @Override
    protected void onBindViewHolder(@NonNull BeaconHolder beaconHolder, int i, @NonNull Beacon beacon) {

        beaconHolder.textViewBeacon.setText(beacon.getBeaconId());
        beaconHolder.textViewcompaId.setText(beacon.getCompId());

    }


    /**
     * The beacons item passed a parameter bellow, contains all needed for a beacon to be shown
     * It creates a beacon per object
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public BeaconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_item,
                parent,false);
        return new BeaconHolder(v);

    }

    /**
     * Beacons get deleted from the database and the view
     * by swiping them
     * @param position
     */
    public void deleteBeacon(int position){

        getSnapshots().getSnapshot(position)
                .getReference().delete();

    }



}
