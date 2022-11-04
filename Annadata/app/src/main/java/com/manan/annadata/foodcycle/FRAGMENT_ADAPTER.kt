package com.manan.annadata.foodcycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.manan.annadata.fragments.BuyMarketFragment
import com.manan.annadata.fragments.PersonalFragment

class FRAGMENT_ADAPTER constructor(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    var TiffinLinkSellerId: String? = null
    public override fun createFragment(position: Int): Fragment {
        if (position == 1) return PersonalFragment()
        val buyMarketFragment: BuyMarketFragment = BuyMarketFragment()
        if (TiffinLinkSellerId != null) {
            val bundle: Bundle = Bundle()
            bundle.putString("TiffinLinkSellerId", TiffinLinkSellerId)
            buyMarketFragment.setArguments(bundle)
        }
        return buyMarketFragment
    }

    public override fun getItemCount(): Int {
        return 2
    }
}