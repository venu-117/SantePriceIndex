package com.sante.priceindex

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sante.priceindex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("SanteApp", "Hello Android")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top level destinations (no back button)
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_login,
                R.id.nav_home,
                R.id.nav_profit_calc,
                R.id.nav_trends
            )
        )

        setupActionBarWithNavController(navController, appBarConfig)
        binding.bottomNav.setupWithNavController(navController)

        // Control UI elements visibility based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_login -> {
                    binding.appBar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.nav_home, R.id.nav_profit_calc, R.id.nav_trends -> {
                    binding.appBar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    binding.appBar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
