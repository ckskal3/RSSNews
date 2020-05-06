package com.ckh.org.rssnews.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ckh.org.rssnews.R
import com.ckh.org.rssnews.fragment.NewsListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.Container, NewsListFragment.getInstance(Bundle()))
            .commit()
    }
    fun changeFragment(link : String){
        val bundle = Bundle()
        bundle.putString("Link",link)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.Container,NewsViewFragment.getInstance(bundle))
//            .addToBackStack(null)
//            .commit()
    }
}
