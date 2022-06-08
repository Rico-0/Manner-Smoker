package com.kapstone.mannersmoker.ui.my.calendar

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.databinding.ItemDailySmokeBinding
import com.kapstone.mannersmoker.model.data.smoke.Smoke
import com.kapstone.mannersmoker.util.DateUtil

class DailySmokeAdapter(
    private val context : Context,
    private val dailySmokeList : List<Smoke>
) : RecyclerView.Adapter<DailySmokeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailySmokeAdapter.ViewHolder {
        val binding =
            ItemDailySmokeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailySmokeAdapter.ViewHolder, position: Int) {
        holder.bindView(dailySmokeList[position])
    }

    override fun getItemCount(): Int = dailySmokeList.size

    inner class ViewHolder(private val binding: ItemDailySmokeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(smoke: Smoke) {
            Glide.with(context)
                .load(R.drawable.smoke_icon)
                .into(binding.smokeIcon)
            binding.smokeTime.text = DateUtil.LocalDateTimeToString(smoke.createDate)
        }
    }
}