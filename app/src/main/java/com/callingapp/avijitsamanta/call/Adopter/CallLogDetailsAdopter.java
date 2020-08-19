package com.callingapp.avijitsamanta.call.Adopter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.callingapp.avijitsamanta.call.Helper.Modal;
import com.callingapp.avijitsamanta.call.R;

import java.util.List;

public class CallLogDetailsAdopter extends RecyclerView.Adapter<CallLogDetailsAdopter.MyDetailsAdopter> {
    private List<Modal> list;
    private Context cts;
    private ItemClickedCallLog click;

    public CallLogDetailsAdopter(List<Modal> list, Context cts, ItemClickedCallLog click) {
        this.list = list;
        this.cts = cts;
        this.click = click;
    }

    @NonNull
    @Override
    public MyDetailsAdopter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cts).inflate(R.layout.call_log_details_item, parent, false);
        return new MyDetailsAdopter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDetailsAdopter holder, int position) {
        holder.imgCallType.setImageResource(list.get(position).getCallType());
        holder.txtDate.setText(list.get(position).getTime());
        holder.txtDuration.setText(list.get(position).getDuration());
        holder.checkBox.setVisibility(list.get(position).isChecked() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyDetailsAdopter extends RecyclerView.ViewHolder {
        ImageView imgCallType, imgSIMType;
        TextView txtDate, txtDuration;
        CheckBox checkBox;

        public MyDetailsAdopter(View itemView) {
            super(itemView);

            imgCallType = itemView.findViewById(R.id.image_view_call_log_details_call_type);
            imgSIMType = itemView.findViewById(R.id.image_view_call_log_details_sim_type);
            txtDate = itemView.findViewById(R.id.text_view_call_log_details_date_time);
            txtDuration = itemView.findViewById(R.id.text_view_call_log_details_call_type_duration);
            checkBox = itemView.findViewById(R.id.check_box_selected_call_log_details);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    list.get(position).setChecked(!list.get(position).isChecked());
                    checkBox.setVisibility(list.get(position).isChecked() ? View.VISIBLE : View.INVISIBLE);
                    click.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface ItemClickedCallLog {
        void onItemClick(int position);
    }

}
