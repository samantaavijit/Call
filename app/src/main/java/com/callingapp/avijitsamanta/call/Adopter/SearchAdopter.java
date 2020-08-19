package com.callingapp.avijitsamanta.call.Adopter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.callingapp.avijitsamanta.call.Helper.ItemClickListener;
import com.callingapp.avijitsamanta.call.Helper.Modal;
import com.callingapp.avijitsamanta.call.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdopter extends RecyclerView.Adapter<SearchAdopter.SView> {
    private List<Modal> list;
    private Context ctx;
    private ItemClickListener listener;

    public SearchAdopter(List<Modal> list, Context ctx, ItemClickListener listener) {
        this.list = list;
        this.ctx = ctx;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.contact_item, parent, false);
        return new SView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SView holder, int position) {
        Modal modal = list.get(position);

        if (modal.getImg() != null) {
            holder.profile.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            Glide.with(ctx)
                    .load(modal.getImg())
                    .into(holder.profile);
        } else {
            holder.profile.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getRandomColor();
            String firstLetter = String.valueOf(modal.getName().charAt(0));
            TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter.toUpperCase(), color);
            holder.imageView.setImageDrawable(textDrawable);
        }
        holder.name.setText(modal.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SView extends RecyclerView.ViewHolder {
        TextView name;
        CircleImageView profile;
        ImageView imageView;

        public SView(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_view_contact_name);
            profile = itemView.findViewById(R.id.circular_image_contact_name);
            imageView = itemView.findViewById(R.id.image_profile_contact_with_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setFilter(List<Modal> modals) {
        list = new ArrayList<>();
        list.addAll(modals);
        notifyDataSetChanged();

    }
}
