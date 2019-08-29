package home;

import android.widget.Filter;

import java.util.ArrayList;

import story.Story;
import story.StoryAdapter;
import story.StoryItemAdapter;

/**
 * Created by victor on 1/7/2018.
 */

public class StoryFilter extends Filter {
    StoryAdapter adapter;
    ArrayList<Story> filterList;


    public StoryFilter(ArrayList<Story> filterList,StoryAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Story> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.stories= (ArrayList<Story>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
