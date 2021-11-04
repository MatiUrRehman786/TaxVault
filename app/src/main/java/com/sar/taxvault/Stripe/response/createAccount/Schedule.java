package com.sar.taxvault.Stripe.response.createAccount;

import com.google.gson.annotations.SerializedName;

public class Schedule{

	@SerializedName("interval")
	private String interval;

	@SerializedName("delay_days")
	private int delayDays;

	public String getInterval(){
		return interval;
	}

	public int getDelayDays(){
		return delayDays;
	}
}