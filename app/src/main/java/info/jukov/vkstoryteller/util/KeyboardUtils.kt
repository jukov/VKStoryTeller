package info.jukov.vkstoryteller.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * User: jukov
 * Date: 18.09.2017
 * Time: 20:22
 */

public fun hideKeyboard(activity: Activity) {

    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    inputMethodManager.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)

}