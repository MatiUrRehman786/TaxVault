package com.sar.taxvault.vm.mo;

import com.sar.taxvault.Model.UserModel;

public class GenericModelLiveData {

    public enum Status { loading, error, success}

    public Object object;

    public Status status;

    public String errorMsg;


    public GenericModelLiveData(Object object, Status status, String errorMsg) {
        this.object = object;
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public GenericModelLiveData() {}
}
