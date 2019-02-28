package com.vlogonappv1.dataclass

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.vlogonappv1.R


class ProgressDialogshow {

    companion object {
    fun progressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.small_progress_bar, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
}