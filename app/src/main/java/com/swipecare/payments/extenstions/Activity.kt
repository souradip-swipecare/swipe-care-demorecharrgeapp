package com.swipecare.payments.extenstions

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

fun Activity.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this,message,duration).show()
}