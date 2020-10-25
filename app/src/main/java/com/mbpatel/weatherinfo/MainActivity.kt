package com.mbpatel.weatherinfo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.mbpatel.weatherinfo.ui.login.LoginActivity
import com.mbpatel.weatherinfo.utils.Constants
import com.mbpatel.weatherinfo.utils.getPreference
import com.mbpatel.weatherinfo.utils.setAlertDialog


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        init()
    }

    private fun init() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map, R.id.nav_history, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Set the Logged Phone number
        val headerView: View = navView.getHeaderView(0)
        val navUser = headerView.findViewById(R.id.edtMobile) as TextView
        navUser.text = getPreference(this, Constants.KEY_PREFERENCE_MOBILE)

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (it.itemId == R.id.nav_logout)
                setAlertDialog(this@MainActivity)
            NavigationUI.onNavDestinationSelected(it, navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}