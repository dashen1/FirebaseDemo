package com.example.easychat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.easychat.model.UserModel
import com.example.easychat.utils.AndroidUtil
import com.example.easychat.utils.FirebaseUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.messaging.FirebaseMessaging

class ProfileFragment : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var usernameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var updateProfileBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var logoutText: TextView

    private var currentUserModel: UserModel? = null

    private var selectedUri: Uri? = null

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profilePic = view.findViewById(R.id.profile_image_view)
        usernameInput = view.findViewById(R.id.profile_username)
        phoneInput = view.findViewById(R.id.profile_phone)
        updateProfileBtn = view.findViewById(R.id.profile_update_btn)
        progressBar = view.findViewById(R.id.profile_progress_bar)
        logoutText = view.findViewById(R.id.profile_logout_text_view)

        progressBar.visibility = View.GONE
        getUserData()

        updateProfileBtn.setOnClickListener {
            updateBtnClick()
        }

        logoutText.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    FirebaseUtil.logout()
                    val intent = Intent(requireContext(), SplashActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }

        profilePic.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 52)
                .createIntent { intent ->
                    imagePickerLauncher.launch(intent)
                }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (data != null && data.data != null) {
                        selectedUri = data.data
                        AndroidUtil.setProfilePic(requireContext(), selectedUri!!, profilePic)
                    }
                }
            }
    }

    private fun updateBtnClick() {
        val newUsername = usernameInput.text.toString().trim()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            usernameInput.error = "Username length should be at least 3 chars."
            return
        }

        currentUserModel?.username = newUsername
        setInProgress(true)

        if (selectedUri != null) {
            FirebaseUtil.getCurrentProfilePicStorage().putFile(selectedUri!!)
                .addOnCompleteListener { _ ->
                    updateToFirebase()
                }
        }else{
            updateToFirebase()
        }

        updateToFirebase()
    }

    private fun updateToFirebase() {
        FirebaseUtil.currentUserDetails().set(currentUserModel!!)
            .addOnCompleteListener { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    AndroidUtil.showToast(requireContext(), "Update successfully.")
                } else {
                    AndroidUtil.showToast(requireContext(), "Update failed.")
                }
            }
    }

    private fun getUserData() {
        setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            currentUserModel = task.result.toObject(UserModel::class.java)
            usernameInput.setText(currentUserModel?.username)
            phoneInput.setText(currentUserModel?.phone)
            setInProgress(false)
        }

    }


    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            updateProfileBtn.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            updateProfileBtn.visibility = View.VISIBLE
        }
    }

}