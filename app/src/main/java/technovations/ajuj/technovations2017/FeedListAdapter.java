package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/23/2017.
 */
import technovations.ajuj.technovations2017.FeedImageView;
import technovations.ajuj.technovations2017.R;
import technovations.ajuj.technovations2017.AppController;
import technovations.ajuj.technovations2017.FeedItem;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    String dorr;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feed_item, null);
            FeedItem item = feedItems.get(position);
            String dorrr = item.getDorr();
            if(dorrr != null && dorrr.equals("receiver"))
                convertView = inflater.inflate(R.layout.receiver_feed_item, null);
        }

        //if (imageLoader == null)
        //    imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        TextView uid = (TextView) convertView.findViewById(R.id.number);
        //NetworkImageView profilePic = (NetworkImageView) convertView
        //        .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);

        FeedItem item = feedItems.get(position);

        name.setText(item.getName());

        uid.setText(item.getUid());

        // Converting timestamp into x ago format
        timestamp.setText(item.getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        url.setText(item.getUrl());

        // user profile pic
        //profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            //feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

}