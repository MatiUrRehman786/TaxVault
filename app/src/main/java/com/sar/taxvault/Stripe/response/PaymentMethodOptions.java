package com.sar.taxvault.Stripe.response;

import com.google.gson.annotations.SerializedName;

public class PaymentMethodOptions{

	@SerializedName("card")
	private Card card;

	public Card getCard(){
		return card;
	}
}