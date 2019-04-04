package com.vlogonappv1.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.vlogonappv1.activity.NewsDetailsFragment
import com.vlogonappv1.dataclass.ContactListItem
import java.io.Serializable

class SlidingFragmentNewsAdapter(private val items: ArrayList<ContactListItem>,
                                 fm: FragmentManager,
                                 private val event: NewsDetailEvents = NewsDetailEvents.home,
                                 private val flag: String? = null,
                                 private val eventId: Int? = null,
                                 private val isRead: String? = null,
                                 private val postType: String? = null)
    : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val postsItem1 = items[position] ?: ContactListItem()
        return when (event) {
            NewsDetailEvents.authorNews,
            NewsDetailEvents.searchNews,
            NewsDetailEvents.newsPaper -> NewsDetailsFragment.newInstance(
                    postsItem1.contactid,
                    postsItem1.contactNumber ?: "")
            else -> NewsDetailsFragment.newInstance(
                    postsItem1.contactid,
                postsItem1.contactNumber ?: ""
            )
        }
    }

    override fun getCount() = items.size
}

enum class NewsDetailEvents : Serializable {
    home, newsPaper, authorNews, searchNews
}