package com.blockchain.commet.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.blockchain.commet.R
import com.blockchain.commet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {

        fun start(context: Context, startId: Int, bundle: Bundle? = null) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("startId", startId)
            bundle?.let {
                intent.putExtras(bundle)
            }
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initNavigationComponent()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackClick()
            }
        })
    }

    /***
     * initNavigationComponent Function
     * get argument received from previous activity as start destination fragment ID
     */
    private fun initNavigationComponent() {
        val startId = intent.extras?.getInt("startId") ?: R.id.conversationsFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_main) as NavHostFragment
        val navController = navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)

        graph.setStartDestination(startId)
        navController.setGraph(graph, intent.extras)
    }

    /***
     * onBackClick function
     * handle process of close fragment
     */
    fun onBackClick() {
        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else
            finish()
    }

}