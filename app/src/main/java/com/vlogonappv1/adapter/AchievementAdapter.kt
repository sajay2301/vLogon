package com.vlogonappv1.adapter

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.dataclass.AchievementsDataClass

import com.vlogonappv1.R
import kotlinx.android.synthetic.main.layout_achievement_view.view.*


class AchievementAdapter (private val itemList: ArrayList<AchievementsDataClass>,
                          private val onItemClick: (position: Int) -> Unit,
                          var activity: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        var commentvideodialog: Dialog? = null
        const val VIEW_TYPE_LOADER = 0
        const val VIEW_TYPE_CONTENT = 1
        private var videoId = ""

    }





    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            (holder as ContentHolder).itemView.apply {
                itemList[position].let {



                    txt_title.text = it!!.title
                    txt_company.text = it!!.company
                    txt_link.text = it!!.link
                    txt_description.text = it!!.discription


                    rl_main.clicks().subscribe{
                        onItemClick(position)
                    }
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (itemList[position] == null) {
            VIEW_TYPE_LOADER
        } else {
            VIEW_TYPE_CONTENT
        }
    }



    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CONTENT -> {
                ContentHolder(inflater.inflate(R.layout.layout_achievement_view, parent, false))

            }
            VIEW_TYPE_LOADER -> {
                LoaderHolder(inflater.inflate(R.layout.item_loader_view, parent, false))
            }
            else -> {
                ContentHolder(inflater.inflate(R.layout.layout_achievement_view, parent, false))
            }
        }

    }

    inner class LoaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
