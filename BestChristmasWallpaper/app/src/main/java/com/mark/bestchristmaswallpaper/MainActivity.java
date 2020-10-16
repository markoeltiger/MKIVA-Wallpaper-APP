package com.mark.bestchristmaswallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<DataHandler> dataHandlerList;
    SwipeRefreshLayout refreshLayout;
    WallpaperaAdapter wallpaperaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list);
        dataHandlerList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        loadData("First");
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData("Refresh");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title,thumbnil ,image;
                title=dataHandlerList.get(i).getTitle();
                image=dataHandlerList.get(i).getImage();
                Intent intent =new Intent(getApplicationContext(),ViewWallpaper.class);
                intent.putExtra("title",title);
                intent.putExtra("image",image);
                startActivity(intent);

            }
        });


    }

    private void loadData(final String type) {
        refreshLayout.setRefreshing(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "https://christmaswallpaperapp.herokuapp.com/apis/", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                refreshLayout.setRefreshing(false);
                parseJSON(response, type);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"a7a"+error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("error",error.getMessage().toString());
            }
        });
        queue.add(request);


    }

    private void parseJSON(String res ,String type) {
        String title ,thumbnail,image ;
        if (type.equals("Refresh")){
            dataHandlerList.clear();
            wallpaperaAdapter.notifyDataSetChanged();
        }
        try {
            JSONArray jsonArray =new JSONArray(res);
            for (int i =0 ;i<jsonArray.length();i++){
                JSONObject  jsonObject =jsonArray.getJSONObject(i);
                title=jsonObject.get("title").toString();
                thumbnail=jsonObject.get("thumbnail").toString();
                image=jsonObject.get("image").toString();
                dataHandlerList.add(new DataHandler(title,thumbnail,image));
                Log.e("adde","done");
            }
            wallpaperaAdapter=new WallpaperaAdapter(getApplicationContext(),R.layout.list_items,dataHandlerList);
            listView.setAdapter(wallpaperaAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
