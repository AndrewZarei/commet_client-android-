package com.blockchain.commet.base

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    //----------------------------------------------------------------------------------------------
    /**
     * onAttach function
     * Called when a fragment is first attached to its context.
     * onCreate(Bundle) will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    //----------------------------------------------------------------------------------------------
}