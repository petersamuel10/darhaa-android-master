package com.itsoluations.vavisa.darhaa.singleton;

import com.itsoluations.vavisa.darhaa.model.favorite.Products;

public class InterfaceProduct {
    public interface OnProductWishChanges {
        void wishChanged();
    }

    private static InterfaceProduct mInstance;

    private OnProductWishChanges mListener;
    private Products product;
    private int position;

    public static InterfaceProduct getInstance() {
        if(mInstance == null)
            mInstance = new InterfaceProduct();
        return mInstance;
    }

    public OnProductWishChanges getmListener() {
        return mListener;
    }

    public void setmListener(OnProductWishChanges mListener) {
        this.mListener = mListener;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        if(mListener != null) {
            this.product = product;
            notifyStateChange();
        }

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void notifyStateChange() {
        mListener.wishChanged();
    }

    private InterfaceProduct() {
    }
}
