package com.nonradioactive.blocr.web;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nonradioactive.blocr.service.CountryService;
import com.nonradioactive.blocr.service.PainSufferingService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BlocrController {
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private PainSufferingService psService;
	
	@GetMapping("/painAndSuffering")
	public Integer getPainAndSuffering(
			@RequestParam String type, 
			@RequestParam String country) {

	    String orgName = countryService.getOrgName(country);
	    return psService.getDefaultAmount(type, orgName);
	}
	
	
	@GetMapping("/painAndSufferingFuture")
	public Integer getPainAndSufferingFuture(
			@RequestParam String type, 
			@RequestParam String country) throws InterruptedException, ExecutionException {

	    String orgName = countryService.getOrgName(country);
	    Future<Integer> typeAmount = psService.getAmountFuture(type, orgName);
	    
//	    Integer amount = typeAmount.get();
	    
	    Future<Integer> defaultAmount = psService.getAmountFuture("OO", orgName);
	    
	    Integer amount = typeAmount.get();
	    return amount + defaultAmount.get();
	}
	
	
	@GetMapping("/painAndSufferingGuava")
	public DeferredResult<Integer> getPainAndSufferingGuava(
			@RequestParam String type, 
			@RequestParam String country) {
		
		final DeferredResult<Integer> ret = new DeferredResult<>();

	    ListenableFuture<String> orgName = countryService.getOrgNameGuava(country);
	    ListenableFuture<Integer> hospitalAmount = Futures.transformAsync(orgName,
	        org -> psService.getAmountGuava("OO", org));

	    ListenableFuture<Integer> userAmount = Futures.transformAsync(orgName, 
	        org -> psService.getAmountGuava(type, org));
	    
	    ListenableFuture<List<Object>> composedFuture = 
	        Futures.allAsList(hospitalAmount , userAmount );

	    Futures.addCallback(composedFuture , new FutureCallback<Object>() {

			@Override
			public void onSuccess(Object result) {
				List<Integer> response = (List<Integer>)result;
				Integer sum = response.stream().reduce(0, Integer::sum);
				ret.setResult(sum);
			}

			@Override
			public void onFailure(Throwable t) {
				ret.setErrorResult(t);
			}
	    }
	    );

	    return ret;
	}
	
	
	@GetMapping("/painAndSufferingAsync")
	public DeferredResult<Integer> getPainAndSufferingAsync(
			@RequestParam String type, 
			@RequestParam String country) {
		
		final DeferredResult<Integer> ret = new DeferredResult<>();

	    CompletableFuture<String> orgName = countryService.getOrgNameAsync(country);
	    
	    CompletableFuture<Integer> hospitalAmount = orgName.thenCompose(orgno -> 
	    	psService.getAmountAsync("OO", orgno));

	    CompletableFuture<Integer> userAmount = orgName.thenCompose(orgno -> 
	    	psService.getAmountAsync(type, orgno));
	    
	    hospitalAmount
	    	.thenCombine(userAmount, (i1, i2) -> i1 + i2)
	    	.thenAccept(response  -> ret.setResult(response))
	    	.exceptionally(ex -> { 
	    		ret.setErrorResult(ex); 
	    		return null; 
	    	});

	    return ret;
	}
	
	
	@GetMapping(path="/getSomePainAndSuffering")
	public Mono<Integer> getSomePain() {
		
		return Mono.just(psService.getDefaultAmount("HS", "SE"));
	}
	
	
	@GetMapping(path="/streamOfPainAndSuffering")
	public Flux<Integer> getStreamOfPainAndSuffering() {
		
		return Flux.just(
					psService.getDefaultAmount("HS", "SE"), 
					psService.getDefaultAmount("OO", "SE"))
				.delayElements(Duration.ofMillis(1000L))
				.map(i -> i+1);
	}
}
