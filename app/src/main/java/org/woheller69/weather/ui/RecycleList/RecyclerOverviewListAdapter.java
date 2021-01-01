package org.woheller69.weather.ui.RecycleList;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.woheller69.weather.R;
import org.woheller69.weather.database.CityToWatch;
import org.woheller69.weather.database.PFASQLiteHelper;
import org.woheller69.weather.preferences.PrefManager;

import java.util.Collections;
import java.util.List;

/**
 * This is the adapter for the RecyclerList that is to be used for the overview of added locations.
 * For the most part, it has been taken from
 * https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.hmhbe8sku
 * as of 2016-08-03
 */
public class RecyclerOverviewListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {

    /**
     * Member variables
     */
    private Context context;
    private static List<CityToWatch> cities;
    PrefManager prefManager;
    PFASQLiteHelper database;


    /**
     * Constructor.
     */
    public RecyclerOverviewListAdapter(Context context, List<CityToWatch> cities) {
        this.context = context;
        RecyclerOverviewListAdapter.cities = cities;
        prefManager = new PrefManager(context);
        database = PFASQLiteHelper.getInstance(context);
    }


    /**
     * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     * Returns the template for a list item.
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_city_list, parent, false);
        return new ItemViewHolder(view);
    }

    /**
     * @see RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     * Sets the content of items.
     */
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.getTvInformation().setText(cities.get(position).getCityName());
        holder.getTvCountryCode().setText(database.getCityById(cities.get(position).getCityId()).getCountryCode());

    }

    /**
     * @see RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return cities.size();
    }

    /**
     * @see ItemTouchHelperAdapter#onItemDismiss(int)
     * Removes an item from the list.
     */
    @Override
    public void onItemDismiss(int position) {
        List<CityToWatch> cityList = getListItems();

        CityToWatch city = cityList.get(position);

        database.deleteCityToWatch(city);

        cities.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * @see ItemTouchHelperAdapter#onItemMove(int, int)
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // For updating the database records
        CityToWatch fromCityToWatch = cities.get(fromPosition);
        int fromRank = fromCityToWatch.getRank();
        CityToWatch toCityToWatch = cities.get(toPosition);
        int toRank = toCityToWatch.getRank();

        fromCityToWatch.setRank(toRank);
        toCityToWatch.setRank(fromRank);
        database.updateCityToWatch(fromCityToWatch);
        database.updateCityToWatch(toCityToWatch);
        Collections.swap(cities, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

    }

    /**
     * @return Returns the items of the list.
     */
    public static List<CityToWatch> getListItems() {
        return cities;
    }


}