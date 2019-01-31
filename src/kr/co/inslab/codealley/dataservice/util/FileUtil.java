package kr.co.inslab.codealley.dataservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 파일 처리 관련 클래스 
 *
 * @author  jdkim
 */
public class FileUtil {

	/**
	 * 파일 copy
	 * @param inFileName
	 * @param outFileName
	 */
	public static void copy(String inFileName, String outFileName) {
		try {
			FileInputStream fis = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);

			int data = 0;
			while((data=fis.read())!=-1) {
				fos.write(data);
			}
			fis.close();
			fos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 디렉토리 내 파일 목록 조회
	 * @param dirPath
	 * @return
	 */
	public static List<File> getDirFileList(String dirPath)
	{
		List<File> dirFileList = null;

		File dir = new File(dirPath);

		if (dir.exists())
		{
			File[] files = dir.listFiles();
			dirFileList = Arrays.asList(files);
		}

		return dirFileList;
	}
	
}
