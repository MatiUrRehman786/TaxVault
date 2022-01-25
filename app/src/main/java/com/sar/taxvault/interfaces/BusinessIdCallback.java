package com.sar.taxvault.interfaces;

import com.sar.taxvault.Model.CustomUserModel;

public interface BusinessIdCallback {

    void onItemClick(String businessId,String businessName);

    void onUserSelected(CustomUserModel user);

}
