package com.simonebakker.simone.addemup.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simonebakker.simone.addemup.R;

public class HighScoreViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout layout;
    public TextView scoreView;
    public TextView dateView;
    public TextView nameView;
    public View view;

    public HighScoreViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout);
        scoreView = itemView.findViewById(R.id.score_view);
        nameView = itemView.findViewById(R.id.name_view);
        dateView = itemView.findViewById(R.id.date_time_view);

        view = itemView;
    }
}
