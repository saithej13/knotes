package com.vst.knotes.MVVM.View.profile

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vst.knotes.R

class WalletProvidedEarningFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.wallet_provided_earning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: access a TextView from your layout
//        val titleTextView = view.findViewById<TextView>(R.id.walletTitleText)
//        titleTextView.text = "Your wa history will show here"
    }
}
