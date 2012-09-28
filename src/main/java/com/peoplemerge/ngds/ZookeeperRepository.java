/************************************************************************
 ** 
 ** Copyright (C) 2011 Dave Thomas, PeopleMerge.
 ** All rights reserved.
 ** Contact: opensource@peoplemerge.com.
 **
 ** This file is part of the NGDS language.
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **    http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 **  
 ** Other Uses
 ** Alternatively, this file may be used in accordance with the terms and
 ** conditions contained in a signed written agreement between you and the 
 ** copyright owner.
 ************************************************************************/
package com.peoplemerge.ngds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZookeeperRepository implements Watcher, ResourceStateRepository {

	private ZooKeeper zk;
	private String rootZnode = "/ngds";

	public ZooKeeper getZookeeper() {
		return zk;
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO handle errors
		System.err.println(event.toString());

	}

	public ZookeeperRepository(String zookeeperConnectString)
			throws IOException {
		zk = new ZooKeeper(zookeeperConnectString, 3000, this);

	}

	@Override
	public String retrieve(String key) {
		Stat stat = null;
		try {
			byte[] bytearray = zk.getData(rootZnode + "/" + key, false, stat);
			String retval = new String(bytearray);
			return retval;
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(String key, String element) {
		try {
			Stat stat = zk.exists(rootZnode + "/" + key, false);
			if (stat == null) {
				zk.create(rootZnode + "/" + key, element.getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} else {
				int version = stat.getVersion();
				zk.setData(rootZnode + "/" + key, element.getBytes(), version);
			}

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void watchData(String key, Watcher observer) {
		try {/*
			 * zk.create(rootZnode + "/" +
			 * key,"".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			 **/
			byte[] dataBytes = zk.getData(rootZnode + "/" + key, observer, null);
			String data = new String(dataBytes);
			System.err.println(data);
		} catch (KeeperException e) {
			// TODO Here we are swallowing the exceptions. FIXME!
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public List<String> watchChildren(String key, Watcher observer) {
		try {/*
			 * zk.create(rootZnode + "/" +
			 * key,"".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			 **/
			return zk.getChildren(rootZnode + "/" + key, observer);
		} catch (KeeperException e) {
			// TODO Here we are swallowing the exceptions. FIXME!
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<String>();

	}
	
	public void delete(String key) {
		try {
			zk.delete(rootZnode + "/" + key, -1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
