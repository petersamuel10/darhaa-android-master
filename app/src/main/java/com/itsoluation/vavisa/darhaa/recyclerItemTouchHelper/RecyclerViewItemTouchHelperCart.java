package com.itsoluation.vavisa.darhaa.recyclerItemTouchHelper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.itsoluation.vavisa.darhaa.Interface.RecyclerItemTouchHelperListner;
import com.itsoluation.vavisa.darhaa.adapter.CartAdapter;

public class RecyclerViewItemTouchHelperCart extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListner listenerCart;

    public RecyclerViewItemTouchHelperCart(int dragDirs, int swipeDirs ,RecyclerItemTouchHelperListner listenerCart) {
        super(dragDirs, swipeDirs);

        this.listenerCart = listenerCart;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if(listenerCart != null)
            listenerCart.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foreground = ((CartAdapter.ViewHolder)viewHolder).foreground;
        getDefaultUIUtil().clearView(foreground);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            View foregroundView = ((CartAdapter.ViewHolder)viewHolder).foreground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreground = ((CartAdapter.ViewHolder)viewHolder).foreground;
        getDefaultUIUtil().onDraw(c,recyclerView,foreground,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreground = ((CartAdapter.ViewHolder)viewHolder).foreground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,foreground,dX,dY,actionState,isCurrentlyActive);
    }
}
