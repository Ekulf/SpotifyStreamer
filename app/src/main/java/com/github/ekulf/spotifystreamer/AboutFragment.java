package com.github.ekulf.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;

public class AboutFragment extends ListFragment {

    public AboutFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new AboutAdapter());
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (position == 1) {
            startActivity(new Intent(getActivity(), LicensesActivity.class));
        }
    }

    public static class AboutAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(
                                parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            TextView text1 = ButterKnife.findById(convertView, android.R.id.text1);
            TextView text2 = ButterKnife.findById(convertView, android.R.id.text2);
            if (position == 0) {
                text1.setText(R.string.title_about_version);
                text2.setText(BuildConfig.VERSION_NAME);
            } else {
                text1.setText(R.string.title_about_licenses);
                text2.setText(R.string.subtitle_about_licenses);
            }

            return convertView;
        }
    }
}
