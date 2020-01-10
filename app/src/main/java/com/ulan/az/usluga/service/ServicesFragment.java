package com.ulan.az.usluga.service;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.FilterListener;
import com.ulan.az.usluga.Main2Activity;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.Searchlistener;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.RVOrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment implements FilterListener, Searchlistener {

    RecyclerView mRecyclerView;

    ArrayList<Service> serviceArrayList;

    RVServiceAdapter adapter;

    DataHelper dataHelper;

    ClientApiListener listener;
    ProgressBar progressBar;

    public ServicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        dataHelper = new DataHelper(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        progressBar = view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 1);

        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.VISIBLE);

        btnAdd.setClickable(true);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("eee","eee");
                startActivityForResult(new Intent(getActivity(), AddServiceActivity.class),1);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {

                if (isOk) {
                    try {
                        Log.e("JSON_Service",json);

                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            boolean bool = true;
                            JSONObject object = jsonArray.getJSONObject(i);
                            //Log.e("Json",jsonArray.length()+"  "+i+object.toString());
                            Service service = new Service();
                            service.setAddress(object.getString("address"));
                            service.setInfo(object.getString("info"));

                            if (!object.isNull("experience"))
                                service.setExperience(object.getDouble("experience"));
                            if (!object.isNull("lat"))
                                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            else service.setGeoPoint(new GeoPoint(0, 0));
                            service.setImage(object.getString("image"));
                            if (object.isNull("sub_category")) continue;
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category")+" -> "+object.getJSONObject("sub_category").getString("sub_category"));
                            service.setId(object.getInt("id"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setId(jsonUser.getInt("id"));
                            user.setDeviceId(jsonUser.getString("device_id"));
                            ArrayList<String> images = new ArrayList<>();

                            if (!object.getString("image1").equals("null")){
                                images.add(object.getString("image1"));
                            } if (!object.getString("image2").equals("null")){
                                images.add(object.getString("image2"));
                            }if (!object.getString("image3").equals("null")){
                                images.add(object.getString("image3"));
                            } if (!object.getString("image4").equals("null")){
                                images.add(object.getString("image4"));
                            } if (!object.getString("image5").equals("null")){
                                images.add(object.getString("image5"));
                            }


                            service.setImages(images);
                            service.setUser(user);

                            serviceArrayList.add(service);

                        }
                        Shared.serviceCategories = serviceArrayList;
                        adapter = new RVServiceAdapter(getContext(), serviceArrayList);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // things to do on the main thread
                                progressBar.setVisibility(View.GONE);
                                mRecyclerView.setAdapter(adapter);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        progressBar.setVisibility(View.VISIBLE);
        if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED, getContext())) {
            Log.e("Category_id", Shared.category_id + "");
            ClientApi.requestGet(URLS.services + "&sub_category=" + E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE,getContext()), listener);
        } else ClientApi.requestGet(URLS.services, listener);


        Main2Activity activity = (Main2Activity) getActivity();

        activity.setServiceApiListener(this, this);

        return view;
    }


    @Override
    public void onFilter(int id) {
        Log.e("Category_id", Shared.category_id + "");
        progressBar.setVisibility(View.VISIBLE);

        if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED, getContext())) {
            Log.e("Category_id", Shared.category_id + "");
            ClientApi.requestGet(URLS.services + "&sub_category=" + E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE,getContext()), listener);
        } else ClientApi.requestGet(URLS.services, listener);

    }


    @Override
    public void onSearch(ArrayList<Service> services) {
        Log.e("SIII",services.size()+"");
        if (services.size()>0) {
            adapter = new RVServiceAdapter(getContext(), services);
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ClientApi.requestGet(URLS.services + "&sub_category=" + E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE,getContext()), listener);
    }
}
