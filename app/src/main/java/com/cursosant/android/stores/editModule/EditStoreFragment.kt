package com.cursosant.android.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cursosant.android.stores.R
import com.cursosant.android.stores.StoreApplication
import com.cursosant.android.stores.common.entities.StoreEntity
import com.cursosant.android.stores.databinding.FragmentEditStoreBinding
import com.cursosant.android.stores.editModule.viewModel.EditStoreViewModel
import com.cursosant.android.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MVVM
        setupViewModel()

        setupTextFields()
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_store_title_edit)
                                            else getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner, {
             mStoreEntity = it
            if (it.id != 0L){
                mIsEditMode = true
                setUiStore(it)
            } else {
                mIsEditMode = false
            }
            setupActionBar()
        })

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner, { result ->
            hideKeyboard()
            when(result){
                is Long -> {
                    mStoreEntity.id = result

                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(
                        mActivity,
                        R.string.edit_store_message_save_success, Toast.LENGTH_SHORT
                    ).show()

                    mActivity?.onBackPressed()
                }
                is StoreEntity -> {
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(mBinding.root,
                        R.string.edit_store_message_update_success,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupTextFields() {
        with(mBinding) {
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)) {
                    with(mStoreEntity) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }

        if (!isValid) Snackbar.make(mBinding.root,
            R.string.edit_store_message_valid,
                Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }

        if (mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }

        if (mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null){
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setShowFab(true)
        mEditStoreViewModel.setResult(Any())

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}