package com.applicake.beanstalkclient.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Plans {
	// (id, repo number, user number, storage)
	TRIAL(12, 1, 1, 100),
	BRONZE(14, 10, 5, 3072),
	SILVER(16, 25, 20, 6144),
	GOLD(18, 50, 40, 12288),
	PLATINIUM(20, 120, 100, 24576),
	DIAMOND(22, 1000, 1000, 61440);
	
	private int planId;
	private int numberOfRepos;
	private int numberOfUsers;
	private int storage;
	
	private static final Map<Integer, Plans> idToPlan = new HashMap<Integer, Plans>();
	
	static {
		for (Plans pl : EnumSet.allOf(Plans.class)){
			idToPlan.put(pl.planId, pl);
		}
	}
	
	
	Plans(int plandId, int numerOfRepos, int numberOfUsers, int storage) {
		this.planId = plandId;
		this.numberOfRepos = numerOfRepos;
		this.numberOfUsers = numberOfUsers;
		this.storage = storage;
	}
	
	public int getPlanId() {
		return planId;
	}


	public int getNumberOfRepos() {
		return numberOfRepos;
	}


	public int getNumberOfUsers() {
		return numberOfUsers;
	}


	public int getStorage() {
		return storage;
	}


	public static Plans getPlanById(int id) {
		return idToPlan.get(id);
	}


}
