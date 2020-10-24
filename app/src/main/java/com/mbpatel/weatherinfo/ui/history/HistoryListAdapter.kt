package com.mbpatel.weatherinfo.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.databinding.RowHistoryListBinding
import com.mbpatel.weatherinfo.db.History

/**
 * Adapter for the [RecyclerView] in [HistoryFragment].
 */
class HistoryListAdapter(private val homeViewModel: HistoryViewModel) :
    ListAdapter<History, HistoryListAdapter.HistoryViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_history_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position), homeViewModel)
    }

    class HistoryViewHolder(private val binding: RowHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.history?.let { item ->
                    binding.viewModel!!.deleteHistory(item)
                }
            }
            binding.imageView.setOnClickListener { productClick(it) }
            binding.txvProductName.setOnClickListener { productClick(it) }
        }

        private fun productClick(view: View) {
            binding.history?.let { product ->
                navigateToProduction(product, view)
            }
        }

        private fun navigateToProduction(
            history: History,
            view: View
        ) {
            val direction =
                HistoryFragmentDirections.actionHistoryFragmentToWeatherFragment(
                    history.latitude.toString(),history.longitude.toString()
                )
            view.findNavController().navigate(direction)
        }

        fun bind(
            item: History,
            hViewModel: HistoryViewModel
        ) {
            with(binding) {
                history = item
                viewModel = hViewModel
                executePendingBindings()
            }
        }
    }
}

private class ProductDiffCallback : DiffUtil.ItemCallback<History>() {

    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}