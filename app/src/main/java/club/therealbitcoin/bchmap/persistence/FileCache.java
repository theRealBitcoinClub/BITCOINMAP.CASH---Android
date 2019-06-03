package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.ACRA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import club.therealbitcoin.bchmap.interfaces.OnTaskDoneListener;



public class FileCache {
    static Map<String, String> cachedContents = new HashMap<String, String>();

    public static void close() {
        cachedContents = new HashMap<String, String>();
    }

    public static String getCachedContentTriggerInit(Context context, String fileName) {
        String cachedString = cachedContents.get(fileName);
        if (cachedString != null) {
            return cachedString;
        }
        File file = getFile(context, fileName);
        if (file.exists()) {
            String cachedFileContent = readFileFromCache(file);
            cachedContents.put(fileName, cachedFileContent);
            return cachedFileContent;
        }
        initFileCache(context, fileName);
        return null;
    }
    private static void initFileCache(Context context, String fileName) {
        File file = getFile(context, fileName);
        if (file.exists()) {
            ifIsUpdatedDataAvailableLoadDataAndStoreInCache(context, fileName);
        } else {
            loadCurrentVersionFromWebAndStoreItIncache(context, fileName);
        }
    }

    @NonNull
    private static File getFile(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + File.separator + fileName + ".json";
        return new File(path);
    }

    private static void loadCurrentVersionFromWebAndStoreItIncache(Context context, String fileName) {
        new WebService("http://raw.githubusercontent.com/theRealBitcoinClub/flutter_coinector/master/assets/" + fileName + ".json", new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String currentData) {
                if (currentData == null || currentData.isEmpty()) {
                    ACRA.log.e("TRBC", "error loadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
                    return;
                }

                writeFileToCache(currentData, getFile(context, fileName));
                ACRA.log.d("TRBC", "success loadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
            }

            @Override
            public void onError() {
                ACRA.log.e("TRBC", "error floadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
            }
        });
    }



    private static String readFileFromCache(File file) {
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while(line != null){
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            return sb.toString();
        } catch (Exception e) {
            ACRA.log.e("TRBC", "Exception reader = new BufferedWriter(new FileWriter(file)) + fileName" + file.getPath());
        } finally {
            try {
                if (buf != null)
                    buf.close();
            } catch (Exception e) {
                ACRA.log.e("TRBC", "Exception reader.close() + fileName" + file.getPath());
            }
        }
        return null;
    }

    private static void writeFileToCache(String currentData, File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentData);
        } catch (Exception e) {
            ACRA.log.e("TRBC", "Exception writer = new BufferedWriter(new FileWriter(file)) + fileName" + file.getPath());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                ACRA.log.e("TRBC", "Exception writer.close() + fileName" + file.getPath());
            }
        }
    }


    public static boolean ifIsUpdatedDataAvailableLoadDataAndStoreInCache(Context context, String fileName) {
        new WebService("http://raw.githubusercontent.com/theRealBitcoinClub/flutter_coinector/master/dataUpdateIncrementVersion.txt", new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String currentVersion) {
                ACRA.log.d("TRBC", "success fetching dataUpdateIncrementVersion + filename:" + fileName);
                int latestVersion = getLastUpdateVersionNumber(context, currentVersion);
                if (latestVersion < Integer.parseInt(currentVersion)) {
                    loadCurrentVersionFromWebAndStoreItIncache(context, fileName);
                } else {
                    ACRA.log.d("TRBC", "update not necessary fetching dataUpdateIncrementVersion + filename:" + fileName);
                }
            }

            @Override
            public void onError() {
                ACRA.log.e("TRBC", "error fetching dataUpdateIncrementVersion + filename:" + fileName);
            }
        });
        return false;
    }

    private static int getLastUpdateVersionNumber(Context context, String currentVersion) {
        String dataVersionCounter = cachedContents.get("dataVersionCounter");
        if (dataVersionCounter != null) {
            return Integer.parseInt(dataVersionCounter);
        }

        File file = getFile(context, "dataVersionCounter");
        String versionNumber = readFileFromCache(file);

        if (versionNumber == null) {
            writeFileToCache(currentVersion,file);
            cachedContents.put("dataVersionCounter", currentVersion);
            return Integer.parseInt(currentVersion);
        }
        cachedContents.put("dataVersionCounter", versionNumber);
        return Integer.parseInt(versionNumber);
    }
}
