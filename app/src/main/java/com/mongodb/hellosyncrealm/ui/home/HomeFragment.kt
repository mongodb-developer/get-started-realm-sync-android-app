package com.mongodb.hellosyncrealm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.mongodb.hellosyncrealm.R
import com.mongodb.hellosyncrealm.RealmDatabase
import com.mongodb.hellosyncrealm.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by navGraphViewModels(
        R.id.mobile_navigation,
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return HomeViewModel() as T
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

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.visitInfoCount.collect {
                binding.textHomeSubtitle.text = "You have visited this page $it times"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            RealmDatabase.isSyncReady.collect { isSyncReady ->
                if (isSyncReady) {
                    homeViewModel.start()
                    binding.pgLoading.visibility = View.GONE
                } else {
                    binding.pgLoading.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}