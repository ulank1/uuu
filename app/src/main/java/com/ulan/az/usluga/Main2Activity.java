package com.ulan.az.usluga;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.Profile.FavoritesActivity;
import com.ulan.az.usluga.Profile.MyForumActivity;
import com.ulan.az.usluga.Profile.MyOrderActivity;
import com.ulan.az.usluga.Profile.MyServiceActivity;
import com.ulan.az.usluga.Profile.ProfileActivity;
import com.ulan.az.usluga.Profile.TenderActivity;
import com.ulan.az.usluga.SubCategory.LocationService;
import com.ulan.az.usluga.forum.ForumCategoryFragment;
import com.ulan.az.usluga.forum.ForumMapActivity;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.helpers.ViewPagerAdapter;
import com.ulan.az.usluga.order.OrderFragment;
import com.ulan.az.usluga.order.OrderMapActivity;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMapActivity;
import com.ulan.az.usluga.service.ServicesFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    ViewPager viewPager;
    private TabLayout tabLayout;
    FragmentManager fm;
    FilterListener orderApiListener, serviceApiListener;
    MaterialSearchView searchView;
    Spinner category, subCategory;
    ArrayList<String> serviceArrayList;
    ArrayList<Category> categoryArrayList;
    ArrayList<Category> subCategoryArrayList;
    ImageView filter, imgMap, imgSearch;
    Switch switch_filter;
    RelativeLayout relativeLayout;
    Context context;
    EditText editSearch;
    int count = 0;
    Searchlistener searchlistenerService, searchlistenerOrder, forumListener;
    LinearLayout lineSearch;
    RelativeLayout relativeRefresh;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;

        Shared.location = new Location("Loc");
        Shared.location.setLatitude(42.8629);
        Shared.location.setLongitude(74.6059);
        checkPermissionsState();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Loh", "Loh");
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10, 0, this);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 10, 0,
                    this);
            Log.e("Loh", "ne_loh");
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(this, "Пожалуйста, включите Геоданные(GPS)",Toast.LENGTH_LONG);
            }
        }
        category = (Spinner) findViewById(R.id.category);
        subCategory = (Spinner) findViewById(R.id.sub_category);
        Log.e("ddd", E.getAppPreferences(E.APP_PREFERENCES_PHOTO, context) + "kk");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });
        final ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        TextView signOut = view.findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                E.setAppPreferences(E.APP_PREFERENCES_PHONE, context, "");
                E.setAppPreferences(E.APP_PREFERENCES_ID, context, "");
                E.setAppPreferences(E.APP_PREFERENCES_NAME, context, "");
                E.setAppPreferences(E.APP_PREFERENCES_PHOTO, context, "");
                E.setAppPreferences(E.APP_PREFERENCES_AGE, context, "");


                startActivity(new Intent(context, SplashActivity.class));
                finish();

            }
        });
        Glide.with(this).load("http://145.239.33.4:5555" + E.getAppPreferences(E.APP_PREFERENCES_PHOTO, context)).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
        Log.e("ddd", E.getAppPreferences(E.APP_PREFERENCES_NAME, context));
        textView.setText(E.getAppPreferences(E.APP_PREFERENCES_NAME, context));

        switch_filter = findViewById(R.id.switch_filter);
        relativeLayout = findViewById(R.id.relative);
        filter = findViewById(R.id.filter);
        imgMap = findViewById(R.id.map);
        editSearch = findViewById(R.id.edit_search);
        lineSearch = findViewById(R.id.line1);
        imgSearch = findViewById(R.id.search);
        relativeRefresh = findViewById(R.id.relative_refresh);


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relativeLayout.setVisibility(View.GONE);

                if (lineSearch.getVisibility() == View.GONE) {
                    lineSearch.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Main2Activity.this, LocationActivity.class));
                } else {
                    lineSearch.setVisibility(View.GONE);
                }

            }
        });

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = v.getText().toString();

                    Shared.search_text = query;
                    Shared.is_search = true;

                    if (viewPager.getCurrentItem() == 0) {
                        ArrayList<Service> categories = new ArrayList<>();

                        for (Service category : Shared.serviceCategories) {
                            String s = category.getDescription().toLowerCase();
                            if (s.contains(query.toLowerCase())) {
                                categories.add(category);
                            }
                        }
                        Collections.sort(categories, new Comparator<Service>() {
                            @Override
                            public int compare(Service lhs, Service rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                //  return Mathlhs.getGeoPoint().getLatitude() > rhs.customInt ? -1 : (lhs.customInt < rhs.customInt) ? 1 : 0;

                                double x1, x2, xe, y1, y2, ye, s1, s2;

                                x1 = lhs.getGeoPoint().getLatitude();
                                x2 = rhs.getGeoPoint().getLatitude();
                                y1 = lhs.getGeoPoint().getLongitude();
                                y2 = rhs.getGeoPoint().getLongitude();
                                xe = Shared.lat_search;
                                ye = Shared.lon_search;

                                s1 = Math.sqrt(Math.abs(x1 - xe) * Math.abs(x1 - xe) + Math.abs(y1 - ye) * Math.abs(y1 - ye));
                                s2 = Math.sqrt(Math.abs(x2 - xe) * Math.abs(x2 - xe) + Math.abs(y2 - ye) * Math.abs(y2 - ye));

                                return Double.compare(s2, s1);

                            }
                        });
                        searchlistenerService.onSearch(categories);
                    } else if (viewPager.getCurrentItem() == 1) {
                        ArrayList<Service> categories = new ArrayList<>();

                        for (Service category : Shared.orderCategories) {
                            String s = category.getDescription().toLowerCase();
                            if (s.contains(query.toLowerCase())) {
                                categories.add(category);
                            }
                        }

                        Collections.sort(categories, new Comparator<Service>() {
                            @Override
                            public int compare(Service lhs, Service rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                //  return Mathlhs.getGeoPoint().getLatitude() > rhs.customInt ? -1 : (lhs.customInt < rhs.customInt) ? 1 : 0;

                                double x1, x2, xe, y1, y2, ye, s1, s2;

                                x1 = lhs.getGeoPoint().getLatitude();
                                x2 = rhs.getGeoPoint().getLatitude();
                                y1 = lhs.getGeoPoint().getLongitude();
                                y2 = rhs.getGeoPoint().getLongitude();
                                xe = Shared.lat_search;
                                ye = Shared.lon_search;

                                s1 = Math.sqrt(Math.abs(x1 - xe) * Math.abs(x1 - xe) + Math.abs(y1 - ye) * Math.abs(y1 - ye));
                                s2 = Math.sqrt(Math.abs(x2 - xe) * Math.abs(x2 - xe) + Math.abs(y2 - ye) * Math.abs(y2 - ye));

                                return Double.compare(s2, s1);

                            }
                        });
                        searchlistenerOrder.onSearch(categories);
                    } else {
                        ArrayList<Category> categories = new ArrayList<>();

                        for (Category category : Shared.forumCategories) {
                            String s = category.getCategory().toLowerCase();
                            Log.e("kkkk", s + " - " + query);
                            if (s.contains(query.toLowerCase())) {
                                categories.add(category);
                            }
                        }
                        Shared.forumCategories_search = categories;
                        forumListener.onSearch(null);
                    }

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    lineSearch.setVisibility(View.GONE);
                    editSearch.setText("");
                    return true;
                }
                return false;
            }


        });


        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    startActivity(new Intent(context, ServiceMapActivity.class));
                } else if (viewPager.getCurrentItem() == 1) {
                    startActivity(new Intent(context, OrderMapActivity.class));
                } else {
                    startActivity(new Intent(context, ForumMapActivity.class));

                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initspinner();

        onChanePagePosition(0);

        Shared.category_id = E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE, this);


        switch_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (viewPager.getCurrentItem()==0){
                    E.setAppPreferences(E.APP_PREFERENCES_FILTER_IS_CHECKED, context, isChecked);
                }else {
                    E.setAppPreferences(E.APP_PREFERENCES_FILTER_IS_CHECKED_ORDER, context, isChecked);
                }

                if (isChecked) {
                    filter.setImageResource(R.drawable.ic_filter);
                    if (viewPager.getCurrentItem() == 0) {
                        serviceApiListener.onFilter(0);
                    } else if (viewPager.getCurrentItem() == 1) {
                        orderApiListener.onFilter(0);
                    }


                } else {
                    filter.setImageResource(R.drawable.ic_filter_passive);

                    if (viewPager.getCurrentItem() == 0) {
                        serviceApiListener.onFilter(0);
                    } else if (viewPager.getCurrentItem() == 1) {
                        orderApiListener.onFilter(0);
                    }
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineSearch.setVisibility(View.GONE);
                if (relativeLayout.getVisibility() == View.GONE) {
                    relativeLayout.setVisibility(View.VISIBLE);
                } else relativeLayout.setVisibility(View.GONE);
            }
        });

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.e("SERA","ddd");
                if (viewPager.getCurrentItem() == 0) {
                    ArrayList<Service> categories = new ArrayList<>();

                    for (Service category : Shared.serviceCategories) {
                        String s = category.getCategory().toLowerCase();
                        if (s.contains(query.toLowerCase())) {
                            categories.add(category);
                        }
                    }
                    searchlistenerService.onSearch(categories);
                } else if (viewPager.getCurrentItem() == 1) {
                    ArrayList<Service> categories = new ArrayList<>();

                    for (Service category : Shared.orderCategories) {
                        String s = category.getCategory().toLowerCase();
                        if (s.contains(query.toLowerCase())) {
                            categories.add(category);
                        }
                    }
                    searchlistenerOrder.onSearch(categories);
                } else {
                    ArrayList<Category> categories = new ArrayList<>();

                    for (Category category : Shared.forumCategories) {
                        String s = category.getCategory().toLowerCase();
                        if (s.contains(query.toLowerCase())) {
                            categories.add(category);
                        }
                    }
                    Shared.forumCategories_search = categories;
                    searchlistenerOrder.onSearch(null);
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        fm = getSupportFragmentManager();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    serviceApiListener.onFilter(0);
                } else if (position == 1) {
                    orderApiListener.onFilter(0);
                }

                onChanePagePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(deviceToken)) {
            //Log.e("TOKK", String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,this)));


            ClientApi.requestPut(deviceToken, this);

            return;
        }


        //Log.e(searchView.toString(),searchView.toString());


        setViewPager();

    }


    private void showGPSDisabledAlertToUser() {

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!E.checkInternetConection(context)) {
            relativeRefresh.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (relativeLayout.getVisibility() == View.GONE)
                super.onBackPressed();
            else relativeLayout.setVisibility(View.GONE);
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            startActivity(new Intent(Main2Activity.this, FavoritesActivity.class));
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(Main2Activity.this, MyOrderActivity.class));

        } else if (id == R.id.nav_forum) {
            startActivity(new Intent(Main2Activity.this, MyForumActivity.class));

        } else if (id == R.id.nav_services) {
            startActivity(new Intent(Main2Activity.this, MyServiceActivity.class));

        } else if (id == R.id.nav_tender) {
            startActivity(new Intent(Main2Activity.this, TenderActivity.class));

        } else if (id==R.id.nav_share){

            String text = "BisSerApp приложение для нахождения заказов для мастеров и мастеров для заказчиков\n https://play.google.com/store/apps/details?id=com.ulan.az.usluga";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(fm);

        Fragment fragment1 = new ServicesFragment();
        fragment1.setArguments(getBundle(1));
        Fragment fragment2 = new OrderFragment();
        fragment2.setArguments(getBundle(2));
        adapter.addFragment(fragment1, "Специалисты");
        adapter.addFragment(fragment2, "Предложения");
        adapter.addFragment(new ForumCategoryFragment(), "Объявления");
        viewPager.setAdapter(adapter);
    }

    private Bundle getBundle(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("tag", type);
        return bundle;
    }


    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int readExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_WIFI_STATE);

        int cameraStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        int callPhoneStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE);


        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                readExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                cameraStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                callPhoneStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Log.e("CheckPermission","TRUE");

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.ACCESS_WIFI_STATE},
                    4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 4: {
                if (grantResults.length > 0) {
                    boolean somePermissionWasDenied = false;
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true;
                        }
                    }

                    if (somePermissionWasDenied) {
                        // Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                    }
                } else {
                    // Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                }


                return;
            }

        }
    }

    public void setOrderApiListener(FilterListener orderApiListener, Searchlistener searchlistener) {
        this.orderApiListener = orderApiListener;
        this.searchlistenerOrder = searchlistener;
    }

    public void setServiceApiListener(FilterListener serviceApiListener, Searchlistener searchlistener) {
        this.serviceApiListener = serviceApiListener;
        this.searchlistenerService = searchlistener;
    }

    public void setForumApiListener(Searchlistener serviceApiListener) {
        this.forumListener = serviceApiListener;
    }

    public void setViewPager() {
        Log.e("Curent", viewPager.getCurrentItem() + "");
        viewPager.getCurrentItem();
    }


    public void initspinner() {


        subCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viewPager.getCurrentItem() == 1)
                    E.setAppPreferences(E.APP_PREFERENCES_FILTER_SUBCATEGORY, context, subCategoryArrayList.get(position-1).getId());
                else if (viewPager.getCurrentItem() == 0)
                    E.setAppPreferences(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE, context, subCategoryArrayList.get(position-1).getId());
                if (position > 0) {
                    for (Category category : subCategoryArrayList) {
                        Log.e("Category", category.getCategory() + " " + category.getId());
                    }
                    E.setAppPreferences(E.APP_PREFERENCES_ID_CATEGORY, context, subCategoryArrayList.get(position - 1).getId());

                    Shared.category_id = subCategoryArrayList.get(position - 1).getId();

                    if (viewPager.getCurrentItem() == 0) {
                        serviceApiListener.onFilter(subCategoryArrayList.get(position - 1).getId());
                    } else if (viewPager.getCurrentItem() == 1) {
                        orderApiListener.onFilter(subCategoryArrayList.get(position - 1).getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    if (viewPager.getCurrentItem() == 1)
                        E.setAppPreferences(E.APP_PREFERENCES_FILTER_CATEGORY, context, position);
                    else if (viewPager.getCurrentItem() == 0) {
                        E.setAppPreferences(E.APP_PREFERENCES_FILTER_CATEGORY_SERVICE, context, position);
                    }
                    ClientApiListener listener = new ClientApiListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("SSSS", json);
                            if (isOk) {
                                try {
                                    int id_ = 0;
                                    int _position = 0;
                                    if (viewPager.getCurrentItem()==0){
                                        id_ = E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY_SERVICE,Main2Activity.this);
                                    }else {
                                        id_ = E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_SUBCATEGORY,Main2Activity.this);

                                    }

                                    JSONObject jsonObject = new JSONObject(json);
                                    ArrayList<String> serviceArrayList = new ArrayList<>();
                                    subCategoryArrayList = new ArrayList<>();

                                    serviceArrayList.add("Подкатегория");
                                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Category category1 = new Category();
                                        category1.setCategory(object.getString("sub_category"));
                                        category1.setId(object.getInt("id"));
                                        if (category1.getId()==id_){
                                            _position = i;
                                        }
                                        subCategoryArrayList.add(category1);

                                        serviceArrayList.add(object.getString("sub_category"));
                                        // Настраиваем адаптер

                                    }

                                    String[] s = serviceArrayList.toArray(new String[serviceArrayList.size()]);

                                    final ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter<String>(Main2Activity.this, R.layout.item_spinner_simple, s);
                                    adapter.setDropDownViewResource(R.layout.item_spinner_simple);

// Вызываем адаптер
                                    final int final_position = _position;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            subCategory.setAdapter(adapter);

                                                if (viewPager.getCurrentItem() == 1)
                                                    subCategory.setSelection(final_position+1);

                                                else if (viewPager.getCurrentItem() == 0)
                                                    subCategory.setSelection(final_position+1);


                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }

                        }
                    };
                    if (viewPager.getCurrentItem() == 1)
                        ClientApi.requestGet(URLS.sub_category + "&category=" + categoryArrayList.get(position - 1).getId(), listener);
                    else if (viewPager.getCurrentItem() == 0) {
                        ClientApi.requestGet(URLS.sub_category_service + "&category=" + categoryArrayList.get(position - 1).getId(), listener);

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ClientApiListener listener1 = new ClientApiListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                //Log.e("SSSS","SSSS");
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        categoryArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        serviceArrayList.add("Категория");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Category category1 = new Category();
                            category1.setCategory(object.getString("category"));
                            category1.setId(object.getInt("id"));
                            categoryArrayList.add(category1);

                            serviceArrayList.add(object.getString("category"));
                            // Настраиваем адаптер

                        }

                        String[] s = serviceArrayList.toArray(new String[serviceArrayList.size()]);

                        final ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(Main2Activity.this, R.layout.item_spinner_simple, s);
                        adapter.setDropDownViewResource(R.layout.item_spinner_simple);

// Вызываем адаптер
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                category.setAdapter(adapter);
                                if (viewPager.getCurrentItem() == 1)
                                    category.setSelection(E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_CATEGORY, context));
                                else if (viewPager.getCurrentItem() == 0)
                                    category.setSelection(E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_CATEGORY_SERVICE, context));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        if (viewPager != null) {
            if (viewPager.getCurrentItem() == 1)
                ClientApi.requestGet(URLS.category, listener1);
            else if (viewPager.getCurrentItem() == 0)
                ClientApi.requestGet(URLS.category_service, listener1);
        } else ClientApi.requestGet(URLS.category_service, listener1);
    }

    public void initSpinnerData() {
        ClientApiListener listener1 = new ClientApiListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                Log.e("SvvvSSS", json);
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        categoryArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        serviceArrayList.add("Категория");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Category category1 = new Category();
                            category1.setCategory(object.getString("category"));
                            category1.setId(object.getInt("id"));
                            categoryArrayList.add(category1);

                            serviceArrayList.add(object.getString("category"));
                            // Настраиваем адаптер

                        }

                        String[] s = serviceArrayList.toArray(new String[serviceArrayList.size()]);

                        final ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(Main2Activity.this, R.layout.item_spinner_simple, s);
                        adapter.setDropDownViewResource(R.layout.item_spinner_simple);

// Вызываем адаптер
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                category.setAdapter(adapter);
                                if (viewPager.getCurrentItem() == 1)
                                    category.setSelection(E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_CATEGORY, context));
                                else if (viewPager.getCurrentItem() == 0)
                                    category.setSelection(E.getAppPreferencesINT(E.APP_PREFERENCES_FILTER_CATEGORY_SERVICE, context));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        if (viewPager != null) {
            if (viewPager.getCurrentItem() == 1)
                ClientApi.requestGet(URLS.category, listener1);
            else if (viewPager.getCurrentItem() == 0)
                ClientApi.requestGet(URLS.category_service, listener1);
        } else ClientApi.requestGet(URLS.category_service, listener1);
    }

    public void onChanePagePosition(int position) {

        Shared.is_search = false;
        if (position==0) {
            switch_filter.setChecked(E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED, this));
            if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED, this)) {
                filter.setImageResource(R.drawable.ic_filter);
            } else {
                filter.setImageResource(R.drawable.ic_filter_passive);
            }
        }else if (position==1){
            switch_filter.setChecked(E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED_ORDER, this));
            if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED_ORDER, this)) {
                filter.setImageResource(R.drawable.ic_filter);
            } else {
                filter.setImageResource(R.drawable.ic_filter_passive);
            }
        }
        initSpinnerData();
        relativeLayout.setVisibility(View.GONE);
        lineSearch.setVisibility(View.GONE);
        imgMap.setVisibility(View.GONE);
        if (position != 2) {
            filter.setVisibility(View.VISIBLE);
            imgMap.setVisibility(View.VISIBLE);
            imgSearch.setVisibility(View.VISIBLE);

        } else {
            filter.setVisibility(View.GONE);
            imgSearch.setVisibility(View.GONE);
        }
    }


    public void onClickRefresh(View view) {

        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 & RESULT_OK == resultCode) {

        } else {
            if (viewPager.getCurrentItem() == 0) serviceApiListener.onFilter(0);
            else if (viewPager.getCurrentItem() == 1) orderApiListener.onFilter(0);
            else if (viewPager.getCurrentItem() == 2) serviceApiListener.onFilter(0);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Shared.location = location;
        Log.e("LOCATION", location.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
