package com.blockchain.commet.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.blockchain.commet.R
import com.blockchain.commet.databinding.ActivityBaseBinding
import com.blockchain.commet.base.BaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

class
 BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private val viewModel: BaseViewModel by viewModels()

    private var TimeBackPressed: Long = 0
    private val TIME_BETWEEN_TWO_BACK = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base)

        onBackClick()
        setupNavigationBottom()

        binding.bottomNavigation.selectedItemId = viewModel.selectedTabId
    }

    private fun setupNavigationBottom() {
        val bottomNavigationMenuView: BottomNavigationMenuView =
            binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView

        // Chat Tab View
        val itemChatView = bottomNavigationMenuView.getChildAt(0) as BottomNavigationItemView
        val tabChatView: View = LayoutInflater.from(this)
            .inflate(R.layout.tab_chat, bottomNavigationMenuView, false)
        itemChatView.addView(tabChatView)

        // Wallet Tab View
        val itemWalletView = bottomNavigationMenuView.getChildAt(1) as BottomNavigationItemView
        val tabWalletView: View = LayoutInflater.from(this)
            .inflate(R.layout.tab_wallet, bottomNavigationMenuView, false)
        itemWalletView.addView(tabWalletView)

        // Setting Tab View
        val itemSettingView = bottomNavigationMenuView.getChildAt(2) as BottomNavigationItemView
        val tabSettingView: View = LayoutInflater.from(this)
            .inflate(R.layout.tab_setting, bottomNavigationMenuView, false)
        itemSettingView.addView(tabSettingView)

        // bind navigation bottom with navigation component
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // set animation for transition between fragments
        val options = NavOptions.Builder()
            .setLaunchSingleTop(false)
            .setEnterAnim(R.anim.fragment_fade_in)
            .setExitAnim(R.anim.fragment_none)
            .build()

        // set animation for select and unselect tabs
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.conversationsFragment -> {
                    viewModel.selectedTabId = R.id.conversationsFragment
                    onSelectTabLayout(tabChatView)
                    onUnSelectTabLayout(tabSettingView)
                    onUnSelectTabLayout(tabWalletView)
                    navController.navigate(R.id.conversationsFragment, null, options)
                }

                R.id.walletFragment -> {
                    viewModel.selectedTabId = R.id.walletFragment
                    onUnSelectTabLayout(tabChatView)
                    onUnSelectTabLayout(tabSettingView)
                    onSelectTabLayout(tabWalletView)
                    navController.navigate(R.id.walletFragment, null, options)
                }

                R.id.settingFragment -> {
                    viewModel.selectedTabId = R.id.settingFragment
                    onSelectTabLayout(tabSettingView)
                    onUnSelectTabLayout(tabChatView)
                    onUnSelectTabLayout(tabWalletView)
                    navController.navigate(R.id.settingFragment, null, options)
                }
            }
            true
        }
    }

    /***
     * private fun onSelectTabLayout
     * increase size of selected tabs icon
     * increase size of selected tabs title
     * play animation of selected tabs icon
     * change color of selected tabs title
     * @param tabView contains view of tab selected
     */
    private fun onSelectTabLayout(tabView: View) {
        val tabIcon =  tabView.findViewById<ImageView>(R.id.nav_img)
        val tabTitle = tabView.findViewById<TextView>(R.id.title)
        tabIcon.setColorFilter(resources.getColor(R.color.colorPrimary))
        tabTitle.setTextColor(resources.getColor(R.color.colorPrimary))
    }


    /***
     * private fun onSelectTabLayout
     * decrease size of selected tabs icon to default
     * decrease size of selected tabs title to default
     * stop animation of selected tabs icon
     * change color of selected tabs title to default
     * @param tabView contains view of tab unselected
     */
    private fun onUnSelectTabLayout(tabView: View) {
        val tabIcon =  tabView.findViewById<ImageView>(R.id.nav_img)
        val tabTitle = tabView.findViewById<TextView>(R.id.title)
        tabIcon.setColorFilter(resources.getColor(R.color.nav_text))
        tabTitle.setTextColor(resources.getColor(R.color.nav_text))
    }

    /***
     * private fun onBackClick
     * handle back pressed by user
     * set time delay between two click
     */
    private fun onBackClick() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (TimeBackPressed + TIME_BETWEEN_TWO_BACK > System.currentTimeMillis() || supportFragmentManager.fragments.size > 1) {
                    finish()
                    return
                } else {
                    Toast.makeText(applicationContext, "Double Touch To Exit..", Toast.LENGTH_SHORT)
                        .show()
                }
                TimeBackPressed = System.currentTimeMillis()
            }
        })
    }
}