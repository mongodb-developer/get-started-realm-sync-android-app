package com.mongodb.hellosyncrealm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.mongodb.hellosyncrealm.HelloRealmSyncApp
import com.mongodb.hellosyncrealm.R
import com.mongodb.hellosyncrealm.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by navGraphViewModels(
        R.id.mobile_navigation,
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val realmApp = (requireActivity().application as HelloRealmSyncApp).realmSync
                    return HomeViewModel(realmApp) as T
                }
            }
        })

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        homeViewModel.visitInfo.observe(viewLifecycleOwner) {
            binding.textHomeSubtitle.text = "You have visited this page $it times"
        }

        binding.btRefreshCount.setOnClickListener {
            homeViewModel.onRefreshCount()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.pgLoading.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}