package com.tkw.omamul.ui.view.water.home.adapter

import android.graphics.Rect
import android.view.View
import android.view.View.MeasureSpec
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.tkw.omamul.R

class SnapDecoration(
    private val screenWidth: Int
): ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val count = parent.adapter!!.itemCount
//        val count = state.itemCount
        //adapter에서 getItemCount + 1로 디폴트 주면
        //항목은 추가 됐으나 pre-layout 상태일 때 itemCount를 호출하게 되어 offset이 잘못 설정되며 스크롤 되지 않는다.
        //https://medium.com/jaesung-dev/android-recyclerview-deep-dive-2-fea902e8b634

        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val offset = (screenWidth / 2) - (view.measuredWidth / 2)
        if (pos == 0) {
            outRect.left = offset
        } else if (pos == count - 1) {
            outRect.right = offset
        }
    }
}