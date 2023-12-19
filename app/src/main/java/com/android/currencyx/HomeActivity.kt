package com.android.currencyx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.RuntimeException

class HomeActivity : AppCompatActivity() {

    lateinit var newsFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val viewPager: ViewPager2 = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> NewsFragment()
                    1 -> CurrencyConverterFragment()
                    2 -> CalculatorFragment()
                    else -> throw RuntimeException()
                }
            }

        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.news -> viewPager.currentItem = 0
                R.id.convert -> viewPager.currentItem = 1
                R.id.calculator -> viewPager.currentItem = 2
                else -> return@setOnNavigationItemSelectedListener false
            }
            return@setOnNavigationItemSelectedListener true
        }

//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.news -> {
//    //                    replaceFragment(NewsFragment())
//                    viewPager.currentItem = 0
//                    return@setOnNavigationItemSelectedListener true
//                }
//                // Add more cases for other menu items if needed
//                else -> false
//            }
//        }.also { viewPager.adapter = it }

        // Show the initial fragment (you may want to show a default fragment)
    }

//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.bottomNavigationView, fragment)
//            .commit()
//    }
}
