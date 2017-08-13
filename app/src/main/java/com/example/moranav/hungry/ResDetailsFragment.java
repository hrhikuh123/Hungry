package com.example.moranav.hungry;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ResDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * this class represent the fragment which responsible of show the details about a restaurant
 */
public class ResDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "id";

    private String id;
    private Restaurant res ;

    private ResDetailFragmentListener mListener;

    public ResDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment ResDetailsFragment.
     */
    public static ResDetailsFragment newInstance(String id) {
        ResDetailsFragment fragment = new ResDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            res = ModelResturant.instance.getRestaurant(id);
            if(res == null)
            {
                Toast toast = Toast.makeText(getActivity(),"There was a problem to edit, please try later again" ,Toast.LENGTH_LONG);
                toast.show();
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_res_details, container, false);
        final ImageView logo = (ImageView) view.findViewById(R.id.res_logo_details);
        final ProgressBar progressBarLogo = (ProgressBar) view.findViewById(R.id.res_logo_details_ProgressBar);
        final ProgressBar progressBarImage = (ProgressBar) view.findViewById(R.id.res_details_image_ProcessBar);
        final ImageView image_res = (ImageView) view.findViewById(R.id.res_images_details);
        TextView name  = (TextView) view.findViewById(R.id.res_name_details);
        TextView description  = (TextView) view.findViewById(R.id.res_description_details);
        TextView address  = (TextView) view.findViewById(R.id.res_address_details);
        TextView phone  = (TextView) view.findViewById(R.id.res_phone_details);
        TextView openHour  = (TextView) view.findViewById(R.id.res_openHour_details);
        Button feedback = (Button) view.findViewById(R.id.res_feedback_details);

        //update restaurant details
        name.setText(res.name);
        description.setText(res.description);
        address.setText(res.address);
        phone.setText(res.phone);
        openHour.setText(res.openHour);

        //if there is a logo
        if(!res.logoURL.isEmpty())
        {
            progressBarLogo.setVisibility(View.VISIBLE);
            ModelResturant.instance.getImage(res.logoURL, new ModelResturant.GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {
                    logo.setImageBitmap(image);
                    progressBarLogo.setVisibility(View.GONE);
                }

                @Override
                public void onFail() {

                }
            });
        }

        //if there is a picture
        if(!res.imageURL.isEmpty())
        {
            progressBarImage.setVisibility(View.VISIBLE);
            ModelResturant.instance.getImage(res.imageURL, new ModelResturant.GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {
                    image_res.setImageBitmap(image);
                    progressBarImage.setVisibility(View.GONE);
                }

                @Override
                public void onFail() {

                }
            });
        }

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFeedBacksButtonClick(res.id);
            }
        });

        return view ;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResDetailFragmentListener) {
            mListener = (ResDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResDetailFragmentListener");
        }
    }
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof ResDetailFragmentListener) {
            mListener = (ResDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResDetailFragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ResDetailFragmentListener {
        void onFeedBacksButtonClick(String resID);
    }
}
