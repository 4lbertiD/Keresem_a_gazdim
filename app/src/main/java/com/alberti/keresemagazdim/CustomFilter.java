package com.alberti.keresemagazdim;

import android.widget.Filter;

import java.util.ArrayList;

public class CustomFilter extends Filter {

    Adapter adapter;
    ArrayList<Pets> filterList;

    public CustomFilter(ArrayList<Pets> filterList,Adapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        if(constraint != null && constraint.length() > 0)
        {

            constraint=constraint.toString().toUpperCase();

            ArrayList<Pets> filteredPets=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {

                if(filterList.get(i).getSpecies().toUpperCase().contains(constraint))
                {
                 filteredPets.add(filterList.get(i));
                }
            }

            results.count=filteredPets.size();
            results.values=filteredPets;

        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.pets= (ArrayList<Pets>) results.values;


        adapter.notifyDataSetChanged();

    }
}