package com.itsoluation.vavisa.darhaa.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoluation.vavisa.darhaa.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<ViewPagerModel> contents;
    private Context context;

    public ViewPagerAdapter(List<ViewPagerModel> contents, Context context) {
        this.contents = contents;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_top, container, false);
        container.addView(view);

        ImageView imageView = view.findViewById(R.id.item_image);
        Picasso.with(context).load(contents.get(position).getImages()).into(imageView);

        TextView name = view.findViewById(R.id.item_desc);
        name.setText(contents.get(position).getNames());

        TextView price = view.findViewById(R.id.item_price);
        price.setText(contents.get(position).getPrices());

        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               // Toast.makeText(context, "vvvvvvvvvv", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}

