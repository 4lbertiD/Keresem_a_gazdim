package com.alberti.keresemagazdim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lista extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private List<Pets> petsList;
    private TextView txt;
    private String usname;
    ApiInterface apiInterface;
    Adapter.RecyclerViewClickListener listener;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        Intent intent = getIntent();


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        progressBar = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recyclerViewX);
        txt = findViewById(R.id.userbox);


        usname = intent.getStringExtra("USERNAME");
        txt.setText("Légy üdvözölve kedves: " + usname +"!");


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        listener = new Adapter.RecyclerViewClickListener() {
            @Override
            public void onRowClick(View view, final int position) {

                if (usname.equals("admin")) {
                    Intent intent = new Intent(Lista.this, EditorActivity.class);
                    intent.putExtra("id", petsList.get(position).getId());
                    intent.putExtra("name", petsList.get(position).getName());
                    intent.putExtra("species", petsList.get(position).getSpecies());
                    intent.putExtra("breed", petsList.get(position).getBreed());
                    intent.putExtra("gender", petsList.get(position).getGender());
                    intent.putExtra("picture", petsList.get(position).getPicture());
                    intent.putExtra("place", petsList.get(position).getPlace());
                    intent.putExtra("datelost", petsList.get(position).getDate());
                    intent.putExtra("username", petsList.get(position).getUsername());
                    intent.putExtra("email", petsList.get(position).getEmail());
                    intent.putExtra("telephone", petsList.get(position).getTelephone());
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(Lista.this, UserActivity.class);
                    intent.putExtra("id", petsList.get(position).getId());
                    intent.putExtra("name", petsList.get(position).getName());
                    intent.putExtra("species", petsList.get(position).getSpecies());
                    intent.putExtra("breed", petsList.get(position).getBreed());
                    intent.putExtra("gender", petsList.get(position).getGender());
                    intent.putExtra("picture", petsList.get(position).getPicture());
                    intent.putExtra("place", petsList.get(position).getPlace());
                    intent.putExtra("datelost", petsList.get(position).getDate());
                    intent.putExtra("username", petsList.get(position).getUsername());
                    intent.putExtra("email", petsList.get(position).getEmail());
                    intent.putExtra("telephone", petsList.get(position).getTelephone());
                    startActivity(intent);
                    }

            }

            @Override
            public void onLoveClick(View view, int position) {

                final int id = petsList.get(position).getId();
                final Boolean love = petsList.get(position).getLove();
                final ImageView mLove = view.findViewById(R.id.love);

                if (love){
                    mLove.setImageResource(R.drawable.likeof);
                    petsList.get(position).setLove(false);
                    updateLove("update_love", id, false);
                    adapter.notifyDataSetChanged();
                } else {
                    mLove.setImageResource(R.drawable.likeon);
                    petsList.get(position).setLove(true);
                    updateLove("update_love", id, true);
                    adapter.notifyDataSetChanged();
                }

            }
        };

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("user", usname);
                startActivity(intent);
                }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );
        searchView.setQueryHint("Írd be a keresett állatfajt...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPets(){

        Call<List<Pets>> call = apiInterface.getPets();
        call.enqueue(new Callback<List<Pets>>() {
            @Override
            public void onResponse(Call<List<Pets>> call, Response<List<Pets>> response) {
                progressBar.setVisibility(View.GONE);
                petsList = response.body();
                Log.i(Lista.class.getSimpleName(), response.body().toString());
                adapter = new Adapter(petsList, Lista.this, listener);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Pets>> call, Throwable t) {
                Toast.makeText(Lista.this, "rp :"+
                                t.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateLove(final String key, final int id, final Boolean love){

        Call<Pets> call = apiInterface.updateLove(key, id, love);

        call.enqueue(new Callback<Pets>() {
            @Override
            public void onResponse(Call<Pets> call, Response<Pets> response) {

                Log.i(Lista.class.getSimpleName(), "Response "+response.toString());

                String value = response.body().getValue();
                String message = response.body().getMassage();

                if (value.equals("1")){
                    Toast.makeText(Lista.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Lista.this, message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Pets> call, Throwable t) {
                Toast.makeText(Lista.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPets();
    }
}