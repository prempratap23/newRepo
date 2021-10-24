package com.e.task.base

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.e.task.utils.RequestPermission


abstract class BaseActivity : AppCompatActivity() {

    var requestPermission: RequestPermission? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        initRequestPermission()
    }
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (null != requestPermission && requestCode == RequestPermission.REQUEST_CODE_PERMISSION)
            requestPermission!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun initRequestPermission() {
        if (null == requestPermission)
            requestPermission = RequestPermission(this, false)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}