package com.sudoajay.pdfviewer.activity.mainActivity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.activity.BaseActivity
import com.sudoajay.pdfviewer.databinding.ActivityMainBinding
import com.sudoajay.pdfviewer.helper.DarkModeBottomSheet

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var isDarkTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        setUpRecyclerView()

        showDarkMode()
    }


    private fun setUpRecyclerView(){
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            it.setTint(
                ContextCompat.getColor(
                    applicationContext, R.color.bottomItemTintColor
                )
            )
            binding.bottomAppBar.navigationIcon = it
        }

        setSupportActionBar(binding.bottomAppBar)
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(MainActivity::class.java.simpleName)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            "darkModeBottomSheet"
        )

    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

}