package com.mbpatel.weatherinfo.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mbpatel.weatherinfo.databinding.FragmentHistoryBinding
import com.mbpatel.weatherinfo.utils.InjectorUtils

class HistoryFragment : Fragment() {


    private val HistoryListViewModel: HistoryViewModel by viewModels {
        InjectorUtils.provideHomeListViewModelFactory(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false).apply {
            viewModel = HistoryListViewModel
            lifecycleOwner = viewLifecycleOwner

            val adapter = HistoryListAdapter(HistoryListViewModel)
            rvSearchList.adapter = adapter
            subscribeUi(adapter)
            HistoryListViewModel.setSearchableHistory(edtSearch.text.toString())

//            val fab: FloatingActionButton = fabAddLocation
//            fab.setOnClickListener {
//                root.findNavController().navigate(R.id.navigation_map)
//            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    HistoryListViewModel.setSearchableHistory(edtSearch.text.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })
        }
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        (activity as MainActivity).supportActionBar?.title = getString(R.string.title_home)
//    }
    private fun subscribeUi(adapter: HistoryListAdapter) {
        HistoryListViewModel.history.observe(viewLifecycleOwner) { product ->
            adapter.submitList(product)
        }
    }
}