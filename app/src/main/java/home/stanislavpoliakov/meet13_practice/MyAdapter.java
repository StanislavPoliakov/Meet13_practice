package home.stanislavpoliakov.meet13_practice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private WDailyData[] data;

    public MyAdapter(WDailyData[] data) {
        this.data = data.clone();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.dailyTime.setText(String.valueOf(data[position].time));
        holder.dailyTempMin.setText(String.valueOf(data[position].temperatureMin));
        holder.dailyTempMax.setText(String.valueOf(data[position].temperatureMax));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void setData(WDailyData[] data) {
        this.data = data.clone();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dailyTime, dailyTempMin, dailyTempMax;

        public MyViewHolder(View itemView) {
            super(itemView);
            dailyTime = itemView.findViewById(R.id.dailyTime);
            dailyTempMin = itemView.findViewById(R.id.dailyTemperatureMin);
            dailyTempMax = itemView.findViewById(R.id.dailyTemperatureMax);
        }
    }
}
