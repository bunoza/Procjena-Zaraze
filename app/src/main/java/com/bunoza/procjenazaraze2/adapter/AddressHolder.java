package com.bunoza.procjenazaraze2.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunoza.procjenazaraze2.R;

public class AddressHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    TextView textView;
    ClickListener clickListener;

    public AddressHolder(@NonNull View itemView, ClickListener clickListener) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        this.clickListener = clickListener;
        textView.setOnClickListener(this);
    }

    public void setName(String name){
        textView.setText(name);
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(textView.getText().toString());
    }
}


