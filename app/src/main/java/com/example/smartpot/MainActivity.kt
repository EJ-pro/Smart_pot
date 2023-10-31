package com.example.smartpot

import Mainpage_Fragment
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mBottomNV: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBottomNV = findViewById(R.id.nav_view)
        mBottomNV.setOnNavigationItemSelectedListener { menuItem ->
            bottomNavigate(menuItem.itemId)
            true
        }
        mBottomNV.selectedItemId = R.id.navigation_1
    }

    private fun bottomNavigate(id: Int) {
        val tag = id.toString()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val currentFragment = fragmentManager.primaryNavigationFragment
        currentFragment?.let { fragmentTransaction.hide(it) }

        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (id) {
                R.id.navigation_1 -> Mainpage_Fragment()
                R.id.navigation_2 -> Plant_Fragment()
                R.id.navigation_3 -> Store_Fragment()
                else -> Mypage_Fragment()
            }
            fragmentTransaction.add(R.id.content_layout, fragment, tag)
        } else {
            fragmentTransaction.show(fragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()
    }
}
