package home.stanislavpoliakov.meet13_practice;

import android.support.v7.util.DiffUtil;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;

public class DiffCall extends DiffUtil.Callback {
    private WDailyData[] oldData, newData;

    public DiffCall(WDailyData[] oldData, WDailyData[] newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.length;
    }

    @Override
    public int getNewListSize() {
        return newData.length;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
