package com.sales.domains.api;

public class EvaluatingPriceParams {
	
	private final transient int param1;
	private final transient int param2;
	private final transient int param3;
	
	public EvaluatingPriceParams(final int param1, final int param2, final int param3){
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}
	
	public int param1(){
		return param1;
	}
	
	public int param2(){
		return param2;
	}
	
	public int param3(){
		return param3;
	}
}
