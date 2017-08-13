package com.example.moranav.hungry;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.Restaurant;
import com.example.moranav.hungry.model.Utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ResAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
* this class represent the fragment which responsible of adding a new restaurant*/
public class ResAddFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "id";

    private String id;
    private Restaurant res;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    boolean logoflag = false ;
    ImageView imageView;
    ImageView logoView;
    Bitmap imageBitmap;
    Bitmap logoBitmap;
    Lock l = new ReentrantLock();
    ProgressBar progressBar;


    TextView logo_lable;
    TextView  image_lable;

    public ResAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment ResAddFragment.
     */
    public static ResAddFragment newInstance(String id) {
        ResAddFragment fragment = new ResAddFragment();
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
        View view = inflater.inflate(R.layout.fragment_res_add, container, false);

        Button save = (Button) view.findViewById(R.id.mainSaveBtn);
        Button cancel = (Button) view.findViewById(R.id.mainCancelBtn);
        Button delete = (Button) view.findViewById(R.id.mainDelBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.mainProgressBar);
        final TextView title = (TextView) view.findViewById(R.id.res_title_add);
        final EditText name = (EditText) view.findViewById(R.id.res_name_add);
        final EditText description = (EditText) view.findViewById(R.id.res_description_add);
        final EditText address = (EditText) view.findViewById(R.id.res_address_add);
        final EditText phone = (EditText) view.findViewById(R.id.res_phone_add);
        final EditText openHour = (EditText) view.findViewById(R.id.res_openHour_add);
        logo_lable = (TextView) view.findViewById(R.id.label_logo_edit);
        image_lable = (TextView) view.findViewById(R.id.label_image_edit);
        final ProgressBar image_progress = (ProgressBar) view.findViewById(R.id.edit_image_ProgressBar);
        final ProgressBar logo_progress = (ProgressBar) view.findViewById(R.id.logo_edit_progressbar);

        imageView = (ImageView)view.findViewById(R.id.mainImageView);
        logoView = (ImageView)view.findViewById(R.id.mainLogoView);

        //if it's a new restaurant
        if (id == null) {
            delete.setVisibility(View.GONE); //the delete button is hidden
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            });
            logoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoflag = true ;
                    dispatchTakePictureIntent();
                }
            });
        }

        //if it's a restaurant for edit
        else {
            res = ModelResturant.instance.getRestaurant(id);
            title.setText("עריכת מסעדה");

            name.setText(res.name, TextView.BufferType.EDITABLE);
            description.setText(res.description, TextView.BufferType.EDITABLE);
            address.setText(res.address, TextView.BufferType.EDITABLE);
            phone.setText(res.phone, TextView.BufferType.EDITABLE);
            openHour.setText(res.openHour, TextView.BufferType.EDITABLE);

            //if there is an image for the restaurant in the storage
            if(!res.imageURL.isEmpty())
            {
                image_progress.setVisibility(View.VISIBLE);
                image_lable.setVisibility(GONE);
                ModelResturant.instance.getImage(res.imageURL, new ModelResturant.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        imageView.setImageBitmap(image);
                        image_progress.setVisibility(GONE);
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            //if there is a logo for the restaurant in the storage
            if(!res.logoURL.isEmpty())
            {
                logo_progress.setVisibility(View.VISIBLE);
                logo_lable.setVisibility(GONE);
                ModelResturant.instance.getImage(res.logoURL, new ModelResturant.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        logoView.setImageBitmap(image);
                        logo_progress.setVisibility(GONE);
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }


        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if at least one of the fields is empty.
                if ((name.getText().toString().equals("")) || (description.getText().toString().equals("")) || (address.getText().toString().equals("")) ||
                        (phone.getText().toString().equals("")) || (openHour.getText().toString().isEmpty()) /*|| (imageBitmap == null && ResAddFragment.this.id==null)|| (logoBitmap == null&& ResAddFragment.this.id==null)*/ ) {
                    Toast t = Toast.makeText(getActivity(), "אנא מלא את כל השדות ", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //add a new restaurant
                if (id == null) {
                    res = new Restaurant();
                    res.ownerID = MainActivity.user.id;
                    res.id=res.ownerID + Utils.getTimeValue(); //random id
                }
                //update the fields
                ResAddFragment.this.res.name = name.getText().toString();
                ResAddFragment.this.res.description = description.getText().toString();
                ResAddFragment.this.res.address = address.getText().toString();
                ResAddFragment.this.res.phone = phone.getText().toString();
                ResAddFragment.this.res.openHour = openHour.getText().toString();

                //if it's a new restaurant
                if(id == null)
                {
                    //save the image
                    if (imageBitmap != null) {

                        //implementation of the interface
                        ModelResturant.instance.saveImage(imageBitmap, "image_" + ResAddFragment.this.res.id + ".jpeg", new ModelResturant.SaveImageListener() {
                            @Override
                            public void complete(String url) {
                                l.lock();
                                ResAddFragment.this.res.imageURL = url;

                                //there was no picture but there is a logo
                                if(!ResAddFragment.this.res.logoURL.isEmpty())
                                {
                                    ModelResturant.instance.addRestaurant(ResAddFragment.this.res);
                                    progressBar.setVisibility(GONE);
                                    ResAddFragment.this.getActivity().getFragmentManager().popBackStack();

                                }
                                l.unlock();

                            }

                            @Override
                            public void fail() {

                            }
                        });
                    }


                    if (logoBitmap != null) {

                        //save the logo
                        ModelResturant.instance.saveImage(logoBitmap, "logo_" + ResAddFragment.this.res.id + ".jpeg", new ModelResturant.SaveImageListener() {
                            @Override
                            public void complete(String url) {
                                l.lock();
                                ResAddFragment.this.res.logoURL = url;

                                //there was no logo but there is a picture
                                if(!ResAddFragment.this.res.imageURL.isEmpty())
                                {
                                    ModelResturant.instance.addRestaurant(ResAddFragment.this.res);
                                    progressBar.setVisibility(GONE);
                                    getFragmentManager().popBackStack();

                                }
                                l.unlock();
                            }

                            @Override
                            public void fail() {

                            }
                        });
                    }

                    if(imageBitmap == null || logoBitmap == null)
                    {
                        ModelResturant.instance.addRestaurant(ResAddFragment.this.res);
                        progressBar.setVisibility(GONE);
                        getFragmentManager().popBackStack();
                    }
                }

                //if edit an existing restaurant
                else{
                    ModelResturant.instance.updateRestaurant(ResAddFragment.this.res);
                    progressBar.setVisibility(GONE);
                    getFragmentManager().popBackStack();
                }


            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the restaurant
                res = ModelResturant.instance.getRestaurant(id);
                ModelResturant.instance.deleteRestaurant(res);
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


        return view;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(!logoflag) {
                imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                image_lable.setVisibility(GONE);
            }else{
                logoBitmap =(Bitmap) extras.get("data");
                logoView.setImageBitmap(logoBitmap);
                logo_lable.setVisibility(GONE);
                logoflag = false ;
            }

        }
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

}
