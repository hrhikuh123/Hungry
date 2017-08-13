package com.example.moranav.hungry;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moranav.hungry.model.ModelFiles;
import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.Restaurant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResListFragment.ResListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ResListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
* this class represent the fragment which responsible of show the restaurant's list*/
public class ResListFragment extends Fragment {

    ListView list;
    List<Restaurant> data;
    ResListAdapter adapter;
    private ResListFragmentListener mListener;

    private static final String ID = "id";
    private String id ;

    public ResListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ResListFragment.
     */
    public static ResListFragment newInstance(String id) {
        ResListFragment fragment = new ResListFragment();
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_res_list, container, false);

        //it's a regular user- show him the full list of restaurants
        if(id == null)
            data = ModelResturant.instance.getAllRestaurants() ;
        //it's a restaurant owner- show him the list of his restaurants
        else
            data = ModelResturant.instance.getAllRestaurantsByOwnerID(id);

        list = (ListView) view.findViewById(R.id.res_list);
        adapter = new ResListAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ID = data.get(position).id ;
                mListener.onClickResRow(ID);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResListFragmentListener) {
            mListener = (ResListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResDetailFragmentListener");
        }
            EventBus.getDefault().register(this);

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof ResListFragmentListener) {
            mListener = (ResListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResDetailFragmentListener");
        }
            EventBus.getDefault().register(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    //update the restaurants list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModelResturant.UpdateRestaurantEvent event) {
        if(id!=null)
            if(!event.res.ownerID.equals(id))
                return ;

        boolean exist = false;
        for (int i = 0 ; i<data.size() ; i++){
            Restaurant r = data.get(i) ;
            if (r.id.equals(event.res.id)){
                exist = true;
                if(event.res.isRemoved == 1) {
                    data.remove(i);
                }
                else
                    data.set(i,event.res);
                break;
            }
        }
        if (!exist && event.res.isRemoved != 1){
            data.add(event.res);
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
    public interface ResListFragmentListener {
        void onClickResRow(String ID);
    }

    //the adapter class
    class ResListAdapter extends BaseAdapter{
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

            if(convertView == null){
                // Inflate the layout for this fragment
                convertView = inflater.inflate(R.layout.res_list_row,null);
            }

            final Restaurant res = data.get(position);
            TextView name  = (TextView) convertView.findViewById(R.id.res_name_row);
            final ImageView imageLogo = (ImageView) convertView.findViewById(R.id.res_image_row);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.resRow_ProgressBar);

            name.setText(res.name);
            imageLogo.setTag(res.logoURL);
            imageLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.res_image, null));

            //if there is a logo
            if(!res.logoURL.isEmpty())
            {
                progressBar.setVisibility(View.VISIBLE);
                ModelResturant.instance.getImage(res.logoURL, new ModelResturant.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String tagUrl = imageLogo.getTag().toString();
                        if (tagUrl.equals(res.logoURL)) {
                            imageLogo.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
            return convertView ;
        }
    }
}


