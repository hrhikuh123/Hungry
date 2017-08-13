package com.example.moranav.hungry;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moranav.hungry.model.FeedBack;
import com.example.moranav.hungry.model.ModelFeedBack;
import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FeedBackAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
* this class represent the fragment which responsible of adding a new feedback*/
public class FeedBackAddFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "ID";
    private static final String RESID = "RESID";

    private String id;
    private String ResId;
    private FeedBack feed = null ;



    public FeedBackAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id .
     * @return A new instance of fragment FeedBackAddFragment.
     */
    public static FeedBackAddFragment newInstance(String id,String ResId) {
        FeedBackAddFragment fragment = new FeedBackAddFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(RESID, ResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            ResId = getArguments().getString(RESID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_back_add, container, false);

        final EditText feedbackET = (EditText) view.findViewById(R.id.feed_feedBack_add);
        final Button save = (Button) view.findViewById(R.id.feed_save_add);
        final TextView title = (TextView) view.findViewById(R.id.feed_title_add);
        final Button delete = (Button) view.findViewById(R.id.feed_delete_add);
        final Button cancel = (Button) view.findViewById(R.id.feed_cancel_add);

        //if editing an existing feedback
        if(id!=null)
        {
            title.setText("ערוך תגובה");
            feed = ModelFeedBack.instance.getFeedBack(id);
            feedbackET.setText(feed.feedback, TextView.BufferType.EDITABLE);
        }
        //if adding a new feedback
        else {
            //hide the delete and cancel buttons
            delete.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedString = feedbackET.getText().toString();

                //if the feedback text isn't empty
                if(feedString != null && !feedString.isEmpty())
                {
                    //create a new feedback
                    FeedBack feed = new FeedBack();
                    feed.ownerID = MainActivity.user.id;
                    feed.ownerName = MainActivity.user.name;
                    feed.feedback  = feedString ;

                    //if edit- update the feedback on the restaurant
                    if(id!=null) {
                        feed.resID = FeedBackAddFragment.this.feed.resID;
                        feed.id = FeedBackAddFragment.this.feed.id;
                        feed.resName = FeedBackAddFragment.this.feed.resName;
                        ModelFeedBack.instance.updateFeedBack(feed);
                    }
                    //if add new feedback- add the new feedback to the list
                    else {
                        feed.resID = FeedBackAddFragment.this.ResId ;
                        feed.id = feed.ownerID + Utils.getTimeValue() ;// ---> add time val to id
                        feed.resName = ModelResturant.instance.getRestaurant(feed.resID).name ;
                        ModelFeedBack.instance.addFeedBack(feed);
                    }


                    feedbackET.setText("", TextView.BufferType.EDITABLE);
                    Toast.makeText(getActivity(),"התגובה נשמרה, תודה לך",Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                    return ;
                }

                //if the feedback text is empty
                else {
                    Toast.makeText(getActivity(),"אנא מלא את שדה התגובה",Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the feedback from the list
                ModelFeedBack.instance.deleteFeedBack(FeedBackAddFragment.this.feed);
                getFragmentManager().popBackStack();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the operation
                getFragmentManager().popBackStack();

            }
        });


        return view ;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.secondary_container).setVisibility(View.GONE);
    }
}
