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
import com.callingapp.avijitsamanta.call.Helper.GeneralItem;
import com.callingapp.avijitsamanta.call.Helper.HeaderItem;
import com.callingapp.avijitsamanta.call.Helper.ListItem;
import com.callingapp.avijitsamanta.call.R;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupedListContactAdopter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionTitleProvider {

    private List<ListItem> listItems;
    private Context ctx;

    public GroupedListContactAdopter(List<ListItem> listItems, Context ctx) {
        this.listItems = listItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ListItem.TYPE_GENERAL) {
            View itemView = inflater.inflate(R.layout.contact_item, parent, false);
            return new GeneralViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.char_item_contact, parent, false);
            return new TextIndicatorView(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof GeneralViewHolder) {
            GeneralItem generalItem = (GeneralItem) listItems.get(position);
            GeneralViewHolder holder = (GeneralViewHolder) viewHolder;

            if (generalItem.getGeneralItem().getImg() != null) {
                holder.profile.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.GONE);
                Glide.with(ctx)
                        .load(generalItem.getGeneralItem().getImg())
                        .into(holder.profile);
            } else {
                holder.profile.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getRandomColor();
                String firstLetter = String.valueOf(generalItem.getGeneralItem().getName().charAt(0));
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter.toUpperCase(), color);
                holder.imageView.setImageDrawable(textDrawable);
            }
            holder.name.setText(generalItem.getGeneralItem().getName());
        } else {
            HeaderItem header = (HeaderItem) listItems.get(position);
            TextIndicatorView holder = (TextIndicatorView) viewHolder;
            holder.textView.setText((header.getHeaderItem().toUpperCase()));
        }

    }

    @Override
    public int getItemCount() {
        return listItems != null ? listItems.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }

    @Override
    public String getSectionTitle(int position) {
        if (listItems.get(position).getType() == ListItem.TYPE_GENERAL) {
            GeneralItem item = (GeneralItem) listItems.get(position);
            return item.getGeneralItem().getName().substring(0, 1);
        }
        return null;
    }

    // ViewHolder for date row item
    class TextIndicatorView extends RecyclerView.ViewHolder {
        TextView textView;

        public TextIndicatorView(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view_char_header);

        }
    }

    // View holder for general row item
    static class GeneralViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView profile;
        ImageView imageView;

        public GeneralViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.text_view_contact_name);
            profile = v.findViewById(R.id.circular_image_contact_name);
            imageView = v.findViewById(R.id.image_profile_contact_with_text);
        }
    }
}
