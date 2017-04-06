package com.nonradioactive.blocr.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Service
public class CountryService {

	private static final String DEFAULT_COUNTRY_CODE = "SE";
	
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);
	
	public String getOrgName(String country) {
		return DEFAULT_COUNTRY_CODE;
	}
	
	
	public ListenableFuture<String> getOrgNameGuava(String country) {
		return ls.schedule(() -> DEFAULT_COUNTRY_CODE,
				0, TimeUnit.MILLISECONDS);
	}
	
	
	public CompletableFuture<String> getOrgNameAsync(String country) {
		
		CompletableFuture<String> result = new CompletableFuture<>();
		
		executor.schedule(() -> { result.complete(DEFAULT_COUNTRY_CODE); },
				0, TimeUnit.MILLISECONDS);
		
		return result;
	}
}
