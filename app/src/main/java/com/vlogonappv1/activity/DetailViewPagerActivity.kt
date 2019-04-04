package com.vlogonappv1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.adapter.NewsDetailEvents
import com.vlogonappv1.adapter.SlidingFragmentNewsAdapter
import com.vlogonappv1.adapter.UnregisterAdapter
import com.vlogonappv1.contactlist.AddressBookActivity
import com.vlogonappv1.dataclass.ContactListItem
import kotlinx.android.synthetic.main.activity_detail_view_pager.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.jetbrains.anko.startActivity


class DetailViewPagerActivity : AppCompatActivity() {
    private var itemList = ArrayList<ContactListItem?>()
    var flag: String = ""
    private var sliderAdapter: SlidingFragmentNewsAdapter? = null


    companion object {

        fun openPostNewsDetails(activity: Activity,
                                items: ArrayList<ContactListItem>,
                                position: Int) {
            activity.startActivity<DetailViewPagerActivity>(
                    Pair("position", position)
            )
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view_pager)

        val position = intent.getIntExtra("position", 0)
        val event = if (intent.hasExtra("event")) {
            intent.getStringExtra("event")
        } else {
            NewsDetailEvents.home.name
        }

        sliderAdapter = SlidingFragmentNewsAdapter(UnregisterAdapter.itemsList, supportFragmentManager,
                NewsDetailEvents.valueOf(event),
                flag
        )
        detailpager!!.adapter = sliderAdapter
        detailpager.currentItem = position
        detailpager.offscreenPageLimit = 1

        detailpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                toolbar?.apply {

                    tvToolbarTitle.text = (position+1).toString() + "/" + UnregisterAdapter.itemsList.size
                    icBack.visibility = View.VISIBLE
                    llActionIcon.visibility = View.GONE
                    icBack.clicks().subscribe {
                        onBackPressed()
                    }
                    setSupportActionBar(this)


                }
            }

        })

        toolbar?.apply {

            tvToolbarTitle.text = (position+1).toString() + "/" + UnregisterAdapter.itemsList.size
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
    }


    override fun onBackPressed() {

        val intent = Intent(this@DetailViewPagerActivity, AddressBookActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()

    }



}
