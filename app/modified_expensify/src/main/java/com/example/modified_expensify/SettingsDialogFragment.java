package com.example.modified_expensify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SettingsDialogFragment extends DialogFragment {

    private ProfileDAO profileDAO;
    private ImageHelper imageHelper;
    private ImageView imgUserAvatar;
    private TextView tvUserName;
    private Button bntEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings_dialog, container, false);

        profileDAO = new ProfileDAO(requireContext());
        imageHelper = new ImageHelper(requireContext());

        tvUserName = view.findViewById(R.id.user_details);
        imgUserAvatar = view.findViewById(R.id.imgUserAvatar);
        bntEditProfile = view.findViewById(R.id.bntEditProfile);

        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnLanguage = view.findViewById(R.id.btnChangeLanguage);
        FirebaseUser user;
        FirebaseAuth auth;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }else{
            tvUserName.setText("Hello " + user.getEmail());
        }

        bntEditProfile.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), ProfileEditActivity.class);
            startActivity(intent);
            dismiss();
        });

        btnLanguage.setOnClickListener(v -> {
            showLanguageDialog();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login.class));
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadUserProfile();
    }
    private void loadUserProfile(){
        UserProfile localProfile = profileDAO.getLocalProfile();

        if (localProfile != null){
            displayUserInfo(localProfile);
        }

        profileDAO.syncProfile(new ProfileDAO.OnProfileSyncListener() {
            @Override
            public void onSyncSuccess() {
                UserProfile updatedProfile = profileDAO.getLocalProfile();
                if (updatedProfile != null) {
                    requireActivity().runOnUiThread(() -> displayUserInfo(updatedProfile));
                }
            }

            @Override
            public void onSyncFailure(String errorMessage) {
            }
        });
    }

    private void displayUserInfo(UserProfile profile){
        tvUserName.setText("Hello " + profile.getFullName());

        if (profile.getAvatar() != null && !profile.getAvatar().equals("default_avatar")) {
            Bitmap avatarBitmap = imageHelper.base64ToBitmap(profile.getAvatar());
            if (avatarBitmap != null) {
                imgUserAvatar.setImageBitmap(avatarBitmap);
            } else {
                imgUserAvatar.setImageResource(R.drawable.default_avatar);
            }
        } else {
            imgUserAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
    private void showLanguageDialog() {
        String[] langs = {"English", "Tiếng Việt"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn ngôn ngữ")
                .setItems(langs, (dialog, which) -> {
                    if (which == 0) {
                        setLocale("vi");
                    } else {
                        setLocale("en");
                    }
                }).show();
    }

    private void setLocale(String langCode) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        prefs.edit().putString("My_Lang", langCode).apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getResources().updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

        // Restart lại MainActivity
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.7),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.END);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.4f); // làm mờ nền
            window.setWindowAnimations(R.style.DialogSlideAnimation); // hiệu ứng trượt
        }
    }
}
