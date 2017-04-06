package com.nonradioactive.blocr.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Service
public class PainSufferingService {
	
	private static Logger LOG = LoggerFactory.getLogger(PainSufferingService.class);
	private ExecutorService es = Executors.newCachedThreadPool();
	
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	
	private final ListeningScheduledExecutorService ls = 
		    MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2));
	
	private final static Integer DEFAULT_AMOUNT = new Integer(41);
	
	
	public Integer getDefaultAmount(String type, String orgName) {
		sleepForOneSecond();
		return DEFAULT_AMOUNT;
	}
	
	

	
	public ListenableFuture<Integer> getAmountGuava(String type, String orgName) {
		return ls.schedule(() -> DEFAULT_AMOUNT, 
		        1000L, TimeUnit.MILLISECONDS);
	}
	
	
	public Future<Integer> getAmountFuture(String type, String orgName) {
		return es.submit(() -> { sleepForOneSecond(); return DEFAULT_AMOUNT;} );
	}
	
	
	@Async
	public Future<Integer> getAmountSpringFuture(String type, String orgName) {
		sleepForOneSecond();
		return new AsyncResult<>(DEFAULT_AMOUNT);
	}
	
	
	public CompletableFuture<Integer> getAmountAsync(String type, String country) {
		
		CompletableFuture<Integer> result = new CompletableFuture<>();
		
		executor.schedule(() -> { result.complete(DEFAULT_AMOUNT); },
				1000L, TimeUnit.MILLISECONDS);
		
		return result;
	}
	
	
	
	private void sleepForOneSecond() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			LOG.info("Couldn't wait for the pain", e);
		}
	}
}
