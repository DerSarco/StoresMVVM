package com.cursosant.android.stores.mainModule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cursosant.android.stores.*
import com.cursosant.android.stores.common.entities.StoreEntity
import com.cursosant.android.stores.databinding.ActivityMainBinding
import com.cursosant.android.stores.editModule.EditStoreFragment
import com.cursosant.android.stores.editModule.viewModel.EditStoreViewModel
import com.cursosant.android.stores.mainModule.adapter.OnClickListener
import com.cursosant.android.stores.mainModule.adapter.StoreAdapter
import com.cursosant.android.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { launchEditFragment() }

        setupViewModel()
        setupRecylcerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this, { stores ->
            mAdapter.setStores(stores)
        })

        mMainViewModel.isShowProgress().observe(this){ isSHowProgress ->
            mBinding.progressBar.visibility = if (isSHowProgress) View.VISIBLE else View.GONE
        }

        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this, { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        })
        mEditStoreViewModel.getStoreSelected().observe(this, { storeEntity ->
            mAdapter.add(storeEntity)
        })
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecylcerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        //getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    /*
    * OnClickListener
    * */
    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_options_title)
                .setItems(items) { _, i ->
                    when(i){
                        0 -> confirmDelete(storeEntity)
                        1 -> dial(storeEntity.phone)
                        2 -> goToWebsite(storeEntity.website)
                    }
                }
                .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm, {dialogInterface, i ->
                mMainViewModel.deleteStore(storeEntity)
            })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        startIntent(callIntent)
    }

    private fun goToWebsite(website: String){
        if (website.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }

            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

}