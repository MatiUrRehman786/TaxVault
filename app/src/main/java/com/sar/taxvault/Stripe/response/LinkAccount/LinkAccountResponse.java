package com.sar.taxvault.Stripe.response.LinkAccount;

import com.google.gson.annotations.SerializedName;

public class LinkAccountResponse{

	@SerializedName("return_data")
	private ReturnData returnData;

	public ReturnData getReturnData(){
		return returnData;
	}
}