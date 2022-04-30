package com.mananhirak.annadata.foodcycle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mananhirak.annadata.fragments.BuyMarketFragment;
import com.mananhirak.annadata.fragments.PersonalFragment;

public class FRAGMENT_ADAPTER extends FragmentStateAdapter {
    public FRAGMENT_ADAPTER(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public String TiffinLinkSellerId;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) return new PersonalFragment();
        BuyMarketFragment buyMarketFragment = new BuyMarketFragment();
        if (TiffinLinkSellerId!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("TiffinLinkSellerId", TiffinLinkSellerId);
            buyMarketFragment.setArguments(bundle);
        }
            return buyMarketFragment;
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
