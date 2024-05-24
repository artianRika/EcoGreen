package com.underoid.ecogreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.underoid.ecogreen.fragments.AccountFragment
import com.underoid.ecogreen.fragments.HomeFragment
import com.underoid.ecogreen.fragments.MapFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val homeFragment = HomeFragment()
        val mapFragment = MapFragment()
        val accountFragment = AccountFragment()
        setCurrentFragment(homeFragment)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment ->setCurrentFragment(homeFragment)
                R.id.mapFragment -> setCurrentFragment(mapFragment)
                R.id.accountFragment -> setCurrentFragment(accountFragment)
            }
            true
        }

    }


    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }

    }
}