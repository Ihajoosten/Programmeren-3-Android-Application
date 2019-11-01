package com.example.element.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.element.activities.ElementDetails;
import com.example.element.R;
import com.example.element.models.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ElementRecyclerViewAdapter extends RecyclerView.Adapter<ElementRecyclerViewAdapter.ElementViewHolder> {

    private Context context;
    private LayoutInflater inflator;

    private List<Element> elementList;
    private RecyclerView rv;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ElementRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Element> elementArrayList) {
        this.context = context;
        this.inflator = layoutInflater;
        this.elementList = elementArrayList;

    }



    public class ElementViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView geo;
        private TextView id;
        private ImageView elementImage;

        public ElementViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvTitle);
            geo = (TextView) view.findViewById(R.id.tvGeo);
            id = (TextView) view.findViewById(R.id.tvIdentification);

            elementImage = (ImageView) view.findViewById(R.id.ivElement);
            rv = (RecyclerView) view.findViewById(R.id.rvElement);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context c = v.getContext();
                    int position = getAdapterPosition();
                    Element element = (Element) elementList.get(position);
                    Intent intent = new Intent(c, ElementDetails.class);
                    intent.putExtra("Element", element);
                    c.startActivity(intent);
                }
            });
        }
    }

    public void updateList(List<Element> newList) {
        elementList = new ArrayList<>();
        elementList.clear();
        elementList.addAll(newList);
        notifyDataSetChanged();
    }

    public void reloadAllData(final List<Element> refreshedList){
        elementList.clear();
        elementList.addAll(refreshedList);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(ElementViewHolder holder, int position) {
        Element e = elementList.get(position);
        holder.title.setText(e.getTitle());
        holder.geo.setText(e.getGeographicalLocation());
        holder.id.setText(e.getIdentification());
        new ImageLoader(holder.elementImage).execute(e.getImageUri());


        holder.elementImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));
        holder.title.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));
        holder.geo.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));
        holder.id.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));
    }

    @SuppressLint("StaticFieldLeak")
    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        ImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            this.imageView.setImageBitmap(result);
        }
    }

    @Override
    public int getItemCount() {
        return this.elementList.size();
    }

    @Override
    public ElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_element_list,parent, false);
        return new ElementViewHolder(v);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
