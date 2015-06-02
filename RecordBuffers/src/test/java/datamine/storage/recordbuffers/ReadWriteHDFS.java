/**
 * Copyright (C) 2015 Turn Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datamine.storage.recordbuffers;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
//import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author yqi
 * @date May 29, 2015
 */
public class ReadWriteHDFS {
	
	Configuration conf = new Configuration();
	FileSystem fs = null;
	
	@BeforeClass
	public void prepareFS() throws IOException {
		File baseDir = new File("./target/hdfs/test").getAbsoluteFile();
		FileUtil.fullyDelete(baseDir);
//		conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
//		MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf);
//		MiniDFSCluster hdfsCluster = builder.build();
//		fs = hdfsCluster.getFileSystem();
//		String hdfsURI = "hdfs://localhost:"+ hdfsCluster.getNameNodePort() + "/";
//		System.out.println(hdfsURI);
	}
	
	@Test
	public void read() {
				
	}
	
	@AfterClass
	public void closeFS() {
		
	}
}
