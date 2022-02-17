package com.mongodb.hellosyncrealm.ui.updateviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mongodb.hellosyncrealm.R
import com.mongodb.hellosyncrealm.databinding.FragmentAddViewsBinding
import com.mongodb.hellosyncrealm.hideKeyboard

class UpdateViewsFragment : Fragment() {


    private val addViewsViewModel by viewModels<UpdateViewsViewModel> {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UpdateViewsViewModel() as T
            }
        }
    }

    private var _binding: FragmentAddViewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddViewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btAddConfirm.setOnClickListener {
            hideKeyboard()
            addViewsViewModel.updateViewCount(binding.etViewCount.text.toString().toInt())
        }

        addViewsViewModel.visitInfo.observe(viewLifecycleOwner) {
            binding.tvViewCount.text = resources.getString(R.string.update_view_count, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}