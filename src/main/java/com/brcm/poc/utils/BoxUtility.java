package com.brcm.poc.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.StringTokenizer;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxSharedLink;
import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;

public class BoxUtility {
	
	
	public static void main(String[] args) {
		try {
			BoxUtility.loadPropertyFile();
			BoxUtility.uploadFileToBox("clmempdata.txt", "//Users//vg950772//Downloads//clmempdata.txt", "testboxapi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static final int MAX_CACHE_ENTRIES = 100;
	static Properties boxProp = null;
	//static final Logger logger = Logger.getLogger(BoxUtility.class);

	public static String uploadFileToBox(String fileName, String filePath, String baseFolder) throws IOException {

		String boxFileUrl = boxProp.getProperty("boxFileUrl");
		String userId = boxProp.getProperty("boxuserId");
		String boxConfigJson = boxProp.getProperty("Boxjson");
		
		BoxAPIConnection boxApiConn = new BoxAPIConnection("pldombvntGENCla1XVXjix9gdVEcNNif");
		//boxApiConn.ref();
		System.out.println(boxApiConn.getProxyUsername());

		Reader reader = new FileReader(Paths.get(boxConfigJson).toFile());
		BoxConfig boxConfig = BoxConfig.readFrom(reader);
		IAccessTokenCache accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES);
		//BoxDeveloperEditionAPIConnection api = BoxDeveloperEditionAPIConnection.getAppUserConnection(userId,boxConfig, accessTokenCache);
		
		

		BoxFolder rootFolder = null;

		BoxItem.Info info = null;
		BoxItem.Info uploadedFileInfo = null;
		BoxFile boxFile = null;
		boolean baseflderexists = false;
		try {
			rootFolder = BoxFolder.getRootFolder(boxApiConn);

			if (baseFolder != null && !"".equals(baseFolder)) {
				for (BoxItem.Info itemInfo : rootFolder) {
					if (baseFolder.equals(itemInfo.getName())) {
						baseflderexists = true;
						rootFolder = new BoxFolder(boxApiConn, itemInfo.getID());
						break;
					}
				}
				if (!baseflderexists) {
					info = rootFolder.createFolder(baseFolder);
					rootFolder = new BoxFolder(boxApiConn, info.getID());
				}
			}
			//System.out.println(rootFolder.getInfo().getSharedLink());
			StringTokenizer st = new StringTokenizer(Paths.get(filePath).getParent().toString(), "//");

			System.out.println("---- Split by // ------");
			BoxFolder chldfldr = rootFolder;
			String flderName;
			while (st.hasMoreElements()) {
				flderName = (String) st.nextElement();
				System.out.println(flderName);
				chldfldr = getChldBoxFolder(boxApiConn, chldfldr, flderName);
				System.out.println("Box folder created--"+chldfldr.getInfo().getName());
			}
			BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
			permissions.setCanDownload(true);
			permissions.setCanPreview(true);
			
			chldfldr.createSharedLink(BoxSharedLink.Access.OPEN, null, permissions);
			System.out.println(chldfldr.getInfo().getSharedLink().getDownloadURL());
			
			
			InputStream fileInputStream = Files.newInputStream(Paths.get(filePath));
			for(BoxItem.Info bxitem : chldfldr) {
				if (fileName.equals(bxitem.getName())  && bxitem instanceof BoxFile.Info) {
					BoxFile bx =   new BoxFile(boxApiConn, bxitem.getID());
	               
	            bx.uploadVersion(fileInputStream);
	            //vers = info.getID();
	           
	            }
			}
			

           
             
            
			
			
			
			uploadedFileInfo = chldfldr.uploadFile(fileInputStream, fileName);

			boxFile = new BoxFile(boxApiConn, uploadedFileInfo.getID());

			boxFileUrl = fileName + boxFile.getInfo().getName();
			System.out.println("Box File URL = " + boxFileUrl);
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return boxFileUrl;
	}

	public static BoxFolder getChldBoxFolder(BoxAPIConnection boxApiConn, BoxFolder parentFldr,
			String boxFldrName) {
		BoxFolder chldFldr = null;
		boolean ischldFldrExists = false;
		for (BoxItem.Info chldInfo : parentFldr.getChildren()) {
			if (boxFldrName.equals(chldInfo.getName())) {
				chldFldr = new BoxFolder(boxApiConn, chldInfo.getID());
				ischldFldrExists = true;
				break;
			}
		}
		System.out.println("Already Exist?"+ischldFldrExists);
		if (!ischldFldrExists) {
			chldFldr = new BoxFolder(boxApiConn, parentFldr.createFolder(boxFldrName).getID());
		}
		return chldFldr;
	}

	public static void loadPropertyFile() throws IOException {
		boxProp = new Properties();
		FileInputStream file = null;
		String propFileName = "/Users/vg950772/POC/TestBOXSpringReactive/src/main/resources/boxconfig.properties";
		try {
			file = new FileInputStream(propFileName);
			boxProp.load(file);
			//PropertyConfigurator.configure(boxProp);
			//logger.info("config File initialized");
		} catch (IOException e) {
			//logger.error(e.getMessage());
			throw e;
		} finally {
			if (file != null)
				file.close();
		}
	}

}
