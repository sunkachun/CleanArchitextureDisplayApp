package com.example.aslcodingtestproject.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.aslcodingtestproject.R
import com.example.aslcodingtestproject.constant.util.OnCustomItemClickListener
import com.example.aslcodingtestproject.databinding.FragmentPhotoThumbnailListBinding
import com.example.aslcodingtestproject.model.remote.responseobj.GetPhotoRespItem
import com.example.aslcodingtestproject.view.adapter.PhotoListAdapter
import com.example.aslcodingtestproject.view.event.OnLoadingEventListener
import com.example.aslcodingtestproject.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class PhotoThumbnailListFragment : Fragment() {

    private var _binding: FragmentPhotoThumbnailListBinding? = null
    private val photoViewModel: PhotoViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var photoListAdapter: PhotoListAdapter
    private var photoDataList: ArrayList<GetPhotoRespItem>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPhotoThumbnailListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.i("onViewCreated")

        init()
        viewModelInit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {

        photoListAdapter = PhotoListAdapter(requireActivity(),
            onCustomItemClickListener = object : OnCustomItemClickListener<GetPhotoRespItem> {
                override fun onClick(view: View?, item: GetPhotoRespItem) {
                    val extras = FragmentNavigatorExtras(view!! to "ivPhotoBig")
                    val args : Bundle = Bundle()
                    args.putParcelable("photoItem", item)

//                    findNavController().navigate(
//                        PhotoThumbnailListFragmentDirections.actionPhotoThumbnailListFragmentToPhotoDetailFragment(
//                            item
//                        )
//                    )
                    findNavController().navigate(
                        resId = R.id.actionPhotoThumbnailListFragmentToPhotoDetailFragment,
                        args = args,
                        navOptions = null,
                        navigatorExtras = extras
                    )
                }
            },
            viewLifecycleOwner
        )
        binding.rvPhotoThumbnail.adapter = photoListAdapter

        binding.llRefresh.setOnRefreshListener {
            getPhotoFromApi()
        }

        binding.ivSearch.setOnClickListener{
            val index = photoDataList?.indexOf(photoViewModel.search(binding.etSearch.text.toString(), photoDataList!!)[0])
            if (index != null) {
                binding.rvPhotoThumbnail.scrollToPosition(index)
            }
        }
    }

    private fun viewModelInit() {
        // Directly observe database data
        photoViewModel.photo.observe(viewLifecycleOwner) { data ->
            Log.d("chris", "photoViewModel.photo.observe(this), data: $data")
            if(data.isNullOrEmpty()){

                getDataFromDatabase()

            }else{
                binding.tvStatus.visibility = View.GONE
                updateUI(data)
                GlobalScope.launch(Dispatchers.IO) {
                    photoViewModel.savePhotoIntoDatabase(data)
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI(data: ArrayList<GetPhotoRespItem>) {
        Log.d("chris", "updateUI: $data")
        data.sortWith { p1, p2 -> p1.title.compareTo(p2.title) }
        photoDataList = data
        photoListAdapter.addList(data)
        photoListAdapter.notifyDataSetChanged()
    }

    private fun getDataFromDatabase() {
        photoViewModel.getPhotoFromDb().observe(viewLifecycleOwner){ data ->
            val list : ArrayList<GetPhotoRespItem> = ArrayList()
            data.forEach { item ->
                list.add(item)
            }
            updateUI(list)
        }
    }

    private fun getPhotoFromApi(){
        photoViewModel.getPhotoFromApi(viewLifecycleOwner,
            onLoadingListener = object : OnLoadingEventListener {
                override fun startLoading() {
                    binding.llRefresh.isRefreshing = true
                }

                override fun stopLoading() {
                    Log.d("chris", "stopLoading(): $this.photo")
                    binding.llRefresh.isRefreshing = false
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getPhotoFromApi()
    }
}