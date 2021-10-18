package com.caijia.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "caijia:test=blacklist", description = "BlackList of IP address")
public class BlackListMBean {

	private Set<String> ips = new HashSet<String>();
	
	@ManagedAttribute(description = "Get IP addresses in blacklist")
	public String[] getBlacklist() {
		return ips.toArray(new String[0]);
	}
	
	@ManagedOperation
	@ManagedOperationParameter(name = "ip", description = "Target IP address that will added to blacklist")
	public void addBlackList(String ip) {
		ips.add(ip);
	}

	@ManagedOperation
	@ManagedOperationParameter(name = "ip", description = "Target IP address that will removed from blacklist")
	public void removeBlackList(String ip) {
		ips.remove(ip);
	}
	
	public boolean shouldBlock(String ip) {
		return ips.contains(ip);
	}
}
