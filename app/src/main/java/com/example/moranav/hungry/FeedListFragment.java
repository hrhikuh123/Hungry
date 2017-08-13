package com.example.moranav.hungry;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moranav.hungry.model.FeedBack;
import com.example.moranav.hungry.model.ModelFeedBack;
import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.Restaurant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedListFragment.FeedBackListListener} interface
 * to handle interaction events.
 * Use the {@link FeedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * this class represent the fragment which responsible of show the feedbacks' list
 */

public class FeedListFragment extends Fragment {
    List<FeedBack> data;
    ListView list;
    FeedBackListAdapter adapter ;

    private static final String ID = "ID";
    private static final String IFOWNERID = "ifOwnerID"; // false for resid and true for Ownerid

    private String id;
    private boolean ifOwnerID;

    private FeedBackListListener mListener;

    public FeedListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param ifOwnerID Parameter 2.
     * @return A new instance of fragment FeedListFragment.
     */
    public static FeedListFragment newInstance(String id,boolean ifOwnerID) {
        FeedListFragment fragment = new FeedListFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putBoolean(IFOWNERID, ifOwnerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            ifOwnerID = getArguments().getBoolean(IFOWNERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);

        //if in the personal area- show the user feedbacks
        if(ifOwnerID)
           data = ModelFeedBack.instance.getAllFeedBackByOwnerID(id);
        //if in the restaurant details- show the feedbacks on the restaurant
        else
            data = ModelFeedBack.instance.getAllFeedBacksByResID(id);

        list = (ListView) view.findViewById(R.id.feedBack_list);

        adapter = new FeedBackListAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onFeedBackClick(data.get(position).id,ifOwnerID);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FeedBackListListener) {
            mListener = (FeedBackListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof FeedBackListListener) {
            mListener = (FeedBackListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;


    }

    //update the feedbacks list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModelFeedBack.UpdateFeedBackEvent event) {
        if(id!=null) {
            if(ifOwnerID) {
                if (!event.feed.ownerID.equals(id))
                    return;
            }else {
                if (!event.feed.resID.equals(id))
                    return;
            }
        }

        boolean exist = false;
        for (int i = 0 ; i<data.size() ; i++){
            FeedBack f = data.get(i) ;
            if (f.id.equals(event.feed.id)){
                exist = true;
                if(event.feed.isRemoved == 1) {
                    data.remove(i);
                }
                else
                    data.set(i,event.feed);
                break;
            }
        }
        if (!exist && event.feed.isRemoved != 1){
            data.add(event.feed);
        }
        adapter.notifyDataSetChanged();
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
    public interface FeedBackListListener {
        void onFeedBackClick(String feedbackId, boolean ifownerID);
    }


    //the adapter class
    class FeedBackListAdapter extends BaseAdapter {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // Inflate the layout for this fragment
                convertView = inflater.inflate(R.layout.feedback_list_row, null);
            }

            TextView ownerName = (TextView) convertView.findViewById(R.id.feed_ownername_row);
            TextView resName = (TextView) convertView.findViewById(R.id.feed_resname_row);
            TextView feedBack = (TextView) convertView.findViewById(R.id.feed_feedback_row);

            //if in personal area- the name of the feedback owner is hidden
            if(ifOwnerID)
                ownerName.setVisibility(View.GONE);
            //if in restaurant details- the name of the restaurant is hidden
            else
                resName.setVisibility(View.GONE);

            FeedBack feed = data.get(position);
            ownerName.setText(feed.ownerName);
            resName.setText(feed.resName);
            feedBack.setText(feed.feedback);

            return convertView;
        }
    }


}
