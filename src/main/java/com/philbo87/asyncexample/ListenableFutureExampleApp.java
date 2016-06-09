package com.philbo87.asyncexample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ListenableFutureExampleApp {
	private static ListeningExecutorService executorService;
	private static CountDownLatch startSignal;

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Welcome to the Listenable Future Example Application.");

		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		startSignal = new CountDownLatch(1);

		List<ListenableFuture<String>> listenableFutureList = new ArrayList<ListenableFuture<String>>();

		// Make 5 futures
		for (int i = 0; i < 5; i++) {
			ListenableFuture<String> future = makeAFuture(i);
			listenableFutureList.add(future);
		}

		Thread.sleep(10000);
		ListenableFuture<List<String>> futureResults = Futures.allAsList(listenableFutureList);
		System.out.println("Telling the CountDownLatch to start");

		startSignal.countDown();

		System.out.println("Waiting for all Futures to complete before access result data...");

		try {
			List<String> listOfFutureCallResults = futureResults.get();
			
			System.out.println("Ready to access result data");
			for (String result : listOfFutureCallResults) {
				System.out.println(result);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		
	}

	private static ListenableFuture<String> makeAFuture(final int number) {
		return executorService.submit(new Callable<String>() {
			public String call() throws Exception {
				// System.out.println("Awaiting start signal in Future
				// "+number);
				startSignal.await();
				Thread.sleep(1000);
				System.out.println("Done awaiting for Future " + number);

				return "Woo";
			}
		});
	}

}
