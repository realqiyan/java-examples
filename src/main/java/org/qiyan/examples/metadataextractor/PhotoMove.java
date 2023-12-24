package org.qiyan.examples.metadataextractor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 照片分类：按照拍摄时间调整照片目录
 * https://github.com/drewnoakes/metadata-extractor
 */
public class PhotoMove {

    public static void move(File sourcePath, String targetPath, boolean onlyLog) {
        AtomicInteger otherCount = new AtomicInteger();

        if ("@Recycle".equals(sourcePath.getName()) || "other".equals(sourcePath.getName())) {
            return;
        }

        File[] files = sourcePath.listFiles();
        if (files.length == 0) {
            try {
                if (!onlyLog) {
                    FileUtils.deleteDirectory(sourcePath);
                }
                System.out.println("delete:" + sourcePath);
            } catch (IOException e) {
                System.err.println("delete error:" + sourcePath);
            }
            return;
        }
        Arrays.stream(files).parallel().forEach(file -> {
            if (file.isDirectory()) {
                move(file, targetPath, onlyLog);
            } else {
                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(file);
                    if (null == metadata) {
                        System.out.println("没有metadata信息：" + file);
                        return;
                    }

                    List<Date> dateList = new ArrayList<>();

                    ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                    extraDate(dateList, exifSubIFDDirectory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                    //extraDate(dateList, exifSubIFDDirectory, ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);

                    ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                    extraDate(dateList, exifIFD0Directory, ExifIFD0Directory.TAG_DATETIME);


                    ExifThumbnailDirectory exifThumbnailDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
                    extraDate(dateList, exifThumbnailDirectory, ExifThumbnailDirectory.TAG_DATETIME);

                    QuickTimeDirectory quickTimeDirectory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
                    extraDate(dateList, quickTimeDirectory, QuickTimeDirectory.TAG_CREATION_TIME);
                    extraDate(dateList, quickTimeDirectory, QuickTimeDirectory.TAG_MODIFICATION_TIME);

                    Mp4Directory mp4Directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
                    extraDate(dateList, mp4Directory, Mp4Directory.TAG_CREATION_TIME);
                    extraDate(dateList, mp4Directory, Mp4Directory.TAG_MODIFICATION_TIME);

                    if (dateList.isEmpty()) {

                        String nameExt = metadata.getFirstDirectoryOfType(FileTypeDirectory.class).getString(FileTypeDirectory.TAG_EXPECTED_FILE_NAME_EXTENSION);
                        File newFile = new File(targetPath + "other" + File.separator + sha1(file) + "." + nameExt);
                        copyFile(file, newFile, onlyLog);
                        return;
                    }

                    dateList.sort((o1, o2) -> o1.getTime() > o2.getTime() ? 1 : -1);

                    Date date = dateList.get(0);

                    //MV
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator);
                    File newFile = new File(targetPath + sdf.format(date) + file.getName());
                    if (newFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                        //System.out.println("skip:"+file);
                        return;
                    }
                    copyFile(file, newFile, onlyLog);


                } catch (Exception e) {
                    System.err.println("ERROR:" + file);
                    System.err.println("ERROR:" + e.getMessage());
                }
            }
        });


    }


    public static void copyFile(File source, File target, boolean onlyLog) {
        try {
            System.out.println("copyFile:" + source + " -> " + target);
            if (!onlyLog) {
                FileUtils.copyFile(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha1(File file) {
        try {
            return DigestUtils.sha1Hex(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void extraDate(List<Date> dateList, Directory directory, int dateFlag) {
        if (null == directory) {
            return;
        }
        Date date = directory.getDate(dateFlag, TimeZone.getTimeZone("Asia/Shanghai"));
        //946656000000 -> 2000-01-01 00:00:00
        if (null != date && date.getTime() > 946656000000L) {
            dateList.add(date);
        }
    }


    public static void main(String[] args) {
        move(new File("D:\\multimedia\\"), "D:\\multimedia-new\\", true);
    }
}
