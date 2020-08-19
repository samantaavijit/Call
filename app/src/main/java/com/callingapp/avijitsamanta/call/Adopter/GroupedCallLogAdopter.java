package com.callingapp.avijitsamanta.call.Adopter;

import android.annotation.SuppressLint;
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
import com.callingapp.avijitsamanta.call.Helper.ItemClickListener;
import com.callingapp.avijitsamanta.call.Helper.ListItem;
import com.callingapp.avijitsamanta.call.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupedCallLogAdopter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> list;
    private Context ctx;
    private ItemClickListener listener;
    private HashMap<String, Integer> hashMap;

    public GroupedCallLogAdopter(List<ListItem> consolidatedList, Context ctx, ItemClickListener listener, HashMap<String, Integer> map) {
        this.list = consolidatedList;
        this.ctx = ctx;
        this.listener = listener;
        this.hashMap = map;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ListItem.TYPE_GENERAL) {
            View itemView = inflater.inflate(R.layout.call_log_item, parent, false);
            return new DisplayViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.char_item_contact, parent, false);
            return new TextHeaderView(itemView);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof DisplayViewHolder) {

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getRandomColor();
            GeneralItem generalItem = (GeneralItem) list.get(position);
            DisplayViewHolder holder = (DisplayViewHolder) viewHolder;
            String name = generalItem.getGeneralItem().getName();
            String date = generalItem.getGeneralItem().getDate();
            String mobileNumber = generalItem.getGeneralItem().getMobile();
            String key = date + mobileNumber;
            if (hashMap.containsKey(key)) {
                int count = hashMap.get(key);

                if (name != null) {

                    if (generalItem.getGeneralItem().getImg() != null) {
                        holder.imageView.setVisibility(View.GONE);
                        holder.circleImageView.setVisibility(View.VISIBLE);
                        Glide.with(ctx)
                                .load(generalItem.getGeneralItem().getImg())
                                .into(holder.circleImageView);
                    } else {
                        holder.imageView.setVisibility(View.VISIBLE);
                        holder.circleImageView.setVisibility(View.GONE);
                        String firstLetter = String.valueOf(generalItem.getGeneralItem().getName().charAt(0));
                        TextDrawable textDrawable = TextDrawable.builder().buildRound(firstLetter.toUpperCase(), color);
                        holder.imageView.setImageDrawable(textDrawable);
                    }
                    if (count != 1)
                        holder.name.setText(name + " (" + count + ")");
                    else
                        holder.name.setText(name);

                    holder.simName.setText("Mobile");
                } else {
                    holder.circleImageView.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.GONE);
                    if (count != 1)
                        holder.name.setText(mobileNumber + " (" + count + ")");
                    else
                        holder.name.setText(mobileNumber);
                    holder.simName.setText("Unsaved");
                    holder.circleImageView.setImageResource(R.drawable.call_profile);
                }
            }

            holder.callType.setImageResource(generalItem.getGeneralItem().getCallType());
            holder.time.setText(generalItem.getGeneralItem().getTime());

        } else {
            HeaderItem header = (HeaderItem) list.get(position);
            TextHeaderView holder = (TextHeaderView) viewHolder;
            holder.textView.setText(header.getHeaderItem());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ViewHolder for date row item
    class TextHeaderView extends RecyclerView.ViewHolder {
        TextView textView;

        public TextHeaderView(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view_char_header);

        }
    }

    // View holder for general row item
    class DisplayViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, simName;
        ImageView callType;
        CircleImageView circleImageView;
        ImageView imageView;

        public DisplayViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_call_log_name);
            time = itemView.findViewById(R.id.text_view_call_log_time);
            simName = itemView.findViewById(R.id.text_view_call_log_sim);
            callType = itemView.findViewById(R.id.image_call_log_call_type);
            circleImageView = itemView.findViewById(R.id.image_call_log_profile);
            imageView = itemView.findViewById(R.id.image_view_call_log_with_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

        }
    }

    public void updateData(List<ListItem> item) {
       list=new ArrayList<>();
        list.addAll(item);
        notifyDataSetChanged();
    }
}
