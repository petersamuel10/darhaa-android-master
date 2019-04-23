package com.itsoluations.vavisa.darhaa.Interface;

import android.view.View;

public interface RecyclerViewItemClickListener {
    void onClick(View view, int position,String product_id,String product_name,int flag);
}