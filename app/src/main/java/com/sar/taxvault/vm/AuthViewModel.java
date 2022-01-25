package com.sar.taxvault.vm;

import android.app.Activity;
import android.util.Log;

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
import com.sar.taxvault.vm.mo.GenericModelLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AuthViewModel extends ViewModel {

    String TAG = "AuthViewModel";

    private MutableLiveData<GenericModelLiveData> data;

    public LiveData<GenericModelLiveData> getLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public void authenticateWithEmailAndPassword(String email, String password, Activity c) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(c, task -> {

                    if (task.isSuccessful()) {

                        getUserModel();

                    } else {

                        if (task.getException() != null) {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to Login Please try again"));

                        }


                    }

                });
    }

    private void getUserModel() {

        FirebaseDatabase.getInstance().getReference("User")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            try {

                                UserModel userModel = snapshot.getValue(UserModel.class);

                                data.postValue(new GenericModelLiveData(userModel, GenericModelLiveData.Status.success, null));

                            } catch (Exception e) {

                                data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "User Not Found"));

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, error.getMessage()));

                    }
                });
    }
//
//    public void loginWithUniqueCode(String code, String password, Activity c) {
//
//        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));
//
//        FirebaseDatabase.getInstance().getReference("User")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//
//                        if (snapshot.exists()) {
//
//                            for (DataSnapshot child : snapshot.getChildren()) {
//
//                                try {
//                                    UserModel userModel = child.getValue(UserModel.class);
//
//                                    if (userModel != null) {
//
//                                        if (code.equalsIgnoreCase(userModel.getUniqueID())) {
//
//                                            authenticateWithEmailAndPassword(userModel.getEmail(), password, c, userModel);
//
//                                            return;
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                    Log.d(TAG, "onDataChange: " + child.getKey());
//
//                                }
//
//
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, error.getMessage()));
//
//                    }
//                });
//    }
}
