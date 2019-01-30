package home.stanislavpoliakov.meet13_practice;

import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "meet13_logs";
    private WDailyData[] data;
    private Callback mActivity;

    public MyAdapter(Context context, WDailyData[] data) {
        //this.data = cloneArray(data);
        this.data = data.clone();
        this.mActivity = (Callback) context;

       // this.data = Arrays.copyOf(data, data.length);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data[position].time);*/
        Date date = new Date(data[position].time * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
        String dateString = dateFormat.format(date);
        //Log.d(TAG, "onBindViewHolder: date = " + dateString);
        holder.dailyTime.setText(dateString);

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.FRANCE);

        double fMin = data[position].temperatureMin;
        long tempMin = Math.round((fMin - 32) * 5 / 9);
        String tMinString = (tempMin > 0) ? ("+" + String.valueOf(tempMin)) : String.valueOf(tempMin);

        double fMax = data[position].temperatureMax;
        long tempMax = Math.round((fMax - 32) * 5 / 9);
        String tMaxString = (tempMax > 0) ? ("+" + String.valueOf(tempMax)) : String.valueOf(tempMax);
        holder.dailyTempMin.setText(String.valueOf(tMinString + "˚С"));
        holder.dailyTempMax.setText(String.valueOf(tMaxString) + "˚С");
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void onNewData(WDailyData[] newData) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffCall(data, newData));
        result.dispatchUpdatesTo(this);

        //data = cloneArray(newData);
        data = newData.clone();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dailyTime, dailyTempMin, dailyTempMax;

        public MyViewHolder(View itemView) {
            super(itemView);
            dailyTime = itemView.findViewById(R.id.dailyTime);
            dailyTempMin = itemView.findViewById(R.id.dailyTemperatureMin);
            dailyTempMax = itemView.findViewById(R.id.dailyTemperatureMax);

            itemView.setOnClickListener((v -> {
                mActivity.viewHolderClicked(getAdapterPosition());
            }));
        }
    }
}
