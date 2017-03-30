/*******************************************************************************
 *   BSD License
 *    
 *   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
 *    
 *   Redistribution and use in source and binary forms, with or without modification, are permitted
 *   provided that the following conditions are met:
 *    
 *   1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. All advertising materials mentioning features or use of this software must display the
 *      following acknowledgement:  This product includes software developed by the AT&T.
 *   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *      promote products derived from this software without specific prior written permission.
 *    
 *   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 *******************************************************************************/
package com.att.ajsc.common.propertysources;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author author
 *
 */
@EnableScheduling
@Component
public class DynamicFileMonitor implements SmartLifecycle {

	private boolean running;
	private WatchService watcher;
	private int phase = Integer.MIN_VALUE;
	private boolean autoStartup = true;

	@Value("${com.att.ajsc.dynamic.properties.path:}")
	private String properties_path;

	@Autowired
	RefreshEndpoint refreshEndpoint;

	final static Logger logger = LoggerFactory.getLogger(DynamicFileMonitor.class);

	public boolean isRunning() {
		return this.running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getPhase() {
		return phase;
	}

	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public synchronized void start() {
		if (!this.running) {
			try {
				this.watcher = FileSystems.getDefault().newWatchService();
			} catch (Exception ex) {
				logger.error("Unable to instantiate filesystem watcher: " + ex);
			}

			if (!properties_path.trim().isEmpty()) {
				String[] paths = properties_path.trim().split(",");

				for (int i = 0; i < paths.length; i++) {
					String path = null;
					if (Paths.get(paths[i]).toFile().getAbsoluteFile().exists()) {
						path = Paths.get(paths[i]).toFile().getAbsolutePath();
					} else if (Paths.get(paths[i]).toFile().exists()) {
						path = Paths.get(paths[i]).toFile().getPath();
					} else if (Paths.get(System.getProperty("user.dir") + paths[i]).toFile().exists()) {
						path = System.getProperty("user.dir") + Paths.get(paths[i]).toFile().getPath();
					} else {
						logger.error("Can't find the specified path: " + paths[i]);
					}

					if (path != null) {
						ResourcePropertySource propertySource = null;
						try {
							propertySource = new ResourcePropertySource("file:" + path);
						} catch (Exception e) {
						}
						if (propertySource != null) {
							Path properties = null;
							try {
								properties = Paths.get(path);
								properties.getParent().register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
										StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
								this.running = true;
							} catch (Exception ex) {
								logger.error("Unable to register " + properties.getParent() + " to filesystem watcher: "
										+ ex);
							}
						}
					}
				}
			}

		}
	}

	@Scheduled(fixedRateString = "${com.att.ajsc.dynamic.watcher.delay:5000}")
	public void poll() {
		WatchKey key = this.watcher.poll();
		boolean refresh = false;
		while (key != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				if (event.context() instanceof Path) {
					logger.debug("Watch event: " + event.kind() + ": context: " + event.context());
					refresh = true;
				}
			}
			key.reset();
			key = this.watcher.poll();
		}

		if (refresh) {
			logger.debug("Marked for refresh, triggering: \n");
			Collection<String> keys = refreshEndpoint.invoke();
			logger.debug("Refreshed keys: " + keys);
		}
	}

	public synchronized void stop() {
		if (this.running) {
			if (this.watcher != null) {
				try {
					this.watcher.close();
				} catch (Exception e) {
					logger.error("Failed to close watcher" + e);
				}
			}
			this.running = false;
		}
	}

	public void stop(Runnable arg0) {
		stop();
		arg0.run();
	}

}
