package technovations.ajuj.technovations2017;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uma on 4/16/2017.
 */
public class LocationItemAdapter extends BaseAdapter {
    private static ArrayList<LocationItem> searchArrayList;

    private LayoutInflater mInflater;

    public LocationItemAdapter(Context context, ArrayList<LocationItem> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Typeface face2 = Typeface.createFromAsset(LocationListView.giveAssets(), "c_gothic.ttf");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview2, null);
            holder = new ViewHolder();
            holder.txtOrg = (TextView) convertView.findViewById(R.id.org);
            holder.txtOrg.setTypeface(face2);
            holder.txtUsername = (TextView) convertView.findViewById(R.id.username1);
            holder.txtUsername.setTypeface(face2);
            holder.txtDistance = (TextView) convertView.findViewById(R.id.distance);
            holder.txtDistance.setTypeface(face2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtOrg.setText(searchArrayList.get(position).getOrg());
        holder.txtUsername.setText(Html.fromHtml(searchArrayList.get(position).getUsername()));
        holder.txtDistance.setText(Html.fromHtml("<b>Distance: </b>" + searchArrayList.get(position).getDistance()));

        return convertView;
    }

    static class ViewHolder {
        TextView txtOrg;
        TextView txtAddress;
        TextView txtPhone;
        TextView txtUsername;
        TextView txtDorr;
        TextView txtDistance;
    }
}