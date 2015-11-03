package com.trevore.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trevore.trevor.R;

/**
 * Created by trevor on 10/8/15.
 */
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView name;

    private User user;

    public UserViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.name);
        itemView.setOnClickListener(this);
    }

    public void bind(User user) {
        this.user = user;

        name.setText(user.name);
    }

    @Override
    public void onClick(View v) {
        // TODO: implement a detail view
    }
}
