package com.jpmc.samplealbum.samplealbumlist;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jpmc.samplealbum.samplealbumlist.RecyclerViewer.Adapter;
import com.jpmc.samplealbum.samplealbumlist.network.GetAlbumDataService;
import com.jpmc.samplealbum.samplealbumlist.network.RetrofitClientInstance;
import com.jpmc.samplealbum.samplealbumlist.roomdatabase.AppDatabase;
import com.jpmc.samplealbum.samplealbumlist.roomdatabase.DatabaseClient;
import com.jpmc.samplealbum.samplealbumlist.roomdatabase.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView mRecyclerView;
    private static ArrayList<SampleAlbum> data;
    ProgressDialog progressDialog;
    private static AppDatabase dbClient;
    private GetAlbumDataService service;
    private List<SampleAlbum> albumList;
    private TextView txtConnectivity;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbClient = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        service = RetrofitClientInstance.getRetrofitInstance().create(GetAlbumDataService.class);

        txtConnectivity = findViewById(R.id.conMessage);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        isConnected = checkInternetConenction();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //System.out.println("Inside Pull to Refresh");
                        isConnected = checkInternetConenction();
                        if(isConnected) {
                            getAlbumData();
                        }
                        else
                        {
                            showOfflineData();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );

        if(isConnected) {
            txtConnectivity.setVisibility(View.GONE);
            progressDialog.show();
            getAlbumData();
        }
        else
        {
                txtConnectivity.setVisibility(View.VISIBLE);
                txtConnectivity.setText(R.string.offline_msg);
                showOfflineData();
        }

    }


    private void getAlbumData()
    {
        Call<List<SampleAlbum>> call = service.getAllAlbums();
        call.enqueue(new Callback<List<SampleAlbum>>() {
            @Override
            public void onResponse(Call<List<SampleAlbum>> call, Response<List<SampleAlbum>> response) {
                albumList = response.body();
                generateAlbumList(response.body());
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                txtConnectivity.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<List<SampleAlbum>> call, Throwable t) {
                 Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                 swipeRefreshLayout.setRefreshing(false);
                 progressDialog.dismiss();
                 txtConnectivity.setVisibility(View.VISIBLE);
                 txtConnectivity.setText(R.string.no_offline_data_msg);
            }
        });

    }

    private void generateAlbumList(List<SampleAlbum> albumList) {

        Collections.sort(albumList, new Comparator<SampleAlbum>() {
            @Override
            public int compare(SampleAlbum e1, SampleAlbum e2) {
                return ((SampleAlbum) e1).getTitle().compareTo(((SampleAlbum) e2).getTitle());
            }
        });

        adapter = new Adapter(albumList);
        mRecyclerView.setAdapter(adapter);
        saveAlbumForOffline saveTask = new saveAlbumForOffline();
        saveTask.execute();
    }  

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }

    class saveAlbumForOffline extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            dbClient.taskDao().deleteTable();
            Task task = new Task();

            for(int i = 0; i < albumList.size(); i++){
                 task.setId(albumList.get(i).getId());
                 task.setTitle(albumList.get(i).getTitle());
                 task.setUserId(albumList.get(i).getUserId());
                 dbClient.taskDao().insert(task);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void showOfflineData() {
        class getOfflineDataTask extends AsyncTask<Void, Void, List<Task>> {

            @Override
            protected List<Task> doInBackground(Void... voids) {
                List<Task> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                List <SampleAlbum> sortStore = new ArrayList<>();

                if(tasks.size()==0)
                {
                     txtConnectivity.setVisibility(View.VISIBLE);
                     txtConnectivity.setText(R.string.no_offline_data_msg);
                }
                else
                {
                    txtConnectivity.setVisibility(View.VISIBLE);
                    txtConnectivity.setText(R.string.offline_msg);
                }

                for (int i = 0; i < tasks.size(); i++) {
                    SampleAlbum e = new SampleAlbum(tasks.get(i).getId(),tasks.get(i).getUserId(),tasks.get(i).getTitle());
                    sortStore.add(e);
                }

                Collections.sort(sortStore, new Comparator<SampleAlbum>() {
                    @Override
                    public int compare(SampleAlbum e1, SampleAlbum e2) {
                        return ((SampleAlbum) e1).getTitle().compareTo(((SampleAlbum) e2).getTitle());
                    }
                 });

                adapter = new Adapter(sortStore);
                mRecyclerView.setAdapter(adapter);

            }
        }

        getOfflineDataTask getDataTask = new getOfflineDataTask();
        getDataTask.execute();
    }
}
