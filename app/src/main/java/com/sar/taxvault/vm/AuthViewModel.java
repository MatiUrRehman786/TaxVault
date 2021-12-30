package com.sar.taxvault.vm;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.activity.Login;
import com.sar.taxvault.vm.mo.GenericModelLiveData;

import org.jetbrains.annotations.NotNull;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data;

    public enum LoginStatus {

        afterTwoFactor,
        beforeTwoFactor

    }

    public LiveData<GenericModelLiveData> getLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public void authenticateWithEmailAndPassword(String email, String password, Activity c, UserModel userModel) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(c, task -> {

                    if (task.isSuccessful()) {

                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.success, null));

                    } else {

                        if (task.getException() != null) {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to Login Please try again"));

                        }


                    }

                });
    }

    public void loginWithUniqueCode(String code, String password, Activity c) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseDatabase.getInstance().getReference("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            for (DataSnapshot child : snapshot.getChildren()) {

                                UserModel userModel = child.getValue(UserModel.class);

                                if (userModel != null) {

                                    if (code.equalsIgnoreCase(userModel.getUniqueID())) {

                                        authenticateWithEmailAndPassword(userModel.getEmail(), password, c, userModel);

                                        return;
                                    }

                                }

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, error.getMessage()));

                    }
                });
    }
}
