package com.blockchain.commet.base

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet : BottomSheetDialogFragment() {
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