/*
 * Copyright (c) 2022.  Bambora ( https://bambora.com/ )
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bambora.android.java.bamborasdk

import android.app.Dialog
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Helper class for displaying the bottom sheets over 80% of the screen.
 */
internal object BottomSheetHelper {
    fun initBottomSheet(dialog: Dialog?, bottomSheetContainer: FrameLayout, displayMetrics: DisplayMetrics) {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            bottomSheetDialog.apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.maxHeight = getMaxHeightOfBottomSheet(displayMetrics)
            }

            val container: FrameLayout = bottomSheetContainer
            BottomSheetBehavior.from(container).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                peekHeight = getMaxHeightOfBottomSheet(displayMetrics)
            }

            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.apply {
                layoutParams.height = getMaxHeightOfBottomSheet(displayMetrics)
                requestLayout()
            }
        }
    }

    private fun getMaxHeightOfBottomSheet(displayMetrics: DisplayMetrics): Int {
        val height = displayMetrics.heightPixels
        return (height * 0.80).toInt()
    }
}
