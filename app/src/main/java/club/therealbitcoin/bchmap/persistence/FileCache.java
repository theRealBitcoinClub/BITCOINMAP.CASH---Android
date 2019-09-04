package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

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
            ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache(context);
            return cachedString;
        }
        File file = getFile(context, fileName);
        if (file.exists()) {
            String cachedFileContent = readFileFromCache(file);
            cachedContents.put(fileName, cachedFileContent);
            ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache(context);
            return cachedFileContent;
        }
        initFileCache(context, fileName);
        return null;
    }
    private static void initFileCache(Context context, String fileName) {
        File file = getFile(context, fileName);
        if (file.exists()) {
            ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache(context);
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
        new WebService("https://raw.githubusercontent.com/theRealBitcoinClub/flutter_coinector/master/assets/" + fileName + ".json", new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String currentData) {
                if (currentData == null || currentData.isEmpty()) {
                    ACRA.log.e("TRBC", "error loadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
                    return;
                }

                writeFileToCache(currentData, getFile(context, fileName));
                cachedContents.put(fileName, currentData);
                //Toast.makeText(context, "App updated succesfully! Please restart!", Toast.LENGTH_LONG).show();
                ACRA.log.d("TRBC", "success loadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
            }

            @Override
            public void onError() {
                ACRA.log.e("TRBC", "error floadCurrentVersionFromWebAndStoreItIncache + filename:" + fileName);
            }
        }).execute();
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


    private static void ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache(Context context) {
        new WebService("https://raw.githubusercontent.com/theRealBitcoinClub/flutter_coinector/master/dataUpdateIncrementVersion.txt", new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String currentVersion) {
                currentVersion = currentVersion.replaceAll("\n","");
                ACRA.log.d("TRBC", "success fetching ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache");
                try {
                    int latestVersion = getAndPersistUpdatedVersionNumber(context, currentVersion);
                    if (latestVersion < Integer.parseInt(currentVersion)) {
                        try {
                            loadCurrentVersionFromWebAndStoreItIncache(context, "places");
                            loadCurrentVersionFromWebAndStoreItIncache(context, "placesId");
                            persistVersionCounter(context, currentVersion);
                        } catch (Exception e) {
                            Toast.makeText(context, "App update failed! Please check your internet connection!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ACRA.log.d("TRBC", "update not necessary ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache");
                    }
                }catch (Exception e) {
                    ACRA.log.e("TRBC", "error forceUpdateNextTime");
                    forceUpdateNextTime(context);
                }
            }

            @Override
            public void onError() {
                ACRA.log.e("TRBC", "error fetching ifIsUpdatedDataAvailableLoadAllDataAndStoreInCache");
            }
        }).execute();
    }

    private static void persistVersionCounter(Context context, String versionNumber) {
        persistNumberAndParseToInt(getFile(context, "dataVersionCounter"), versionNumber);
    }

    private static void forceUpdateNextTime(Context context) {
        persistVersionCounter(context,"0");
    }

    private static int getAndPersistUpdatedVersionNumber(Context context, String currentVersion) {
        File file = getFile(context, "dataVersionCounter");
        String versionNumber = null;
        if (file.exists()) {
            versionNumber = readFileFromCache(file);
        }

        if (versionNumber == null || versionNumber.isEmpty()) {
            return persistNumberAndParseToInt(file, currentVersion);
        }
        return Integer.parseInt(versionNumber.replaceAll("\n",""));
    }

    private static int persistNumberAndParseToInt(File file, String versionNumber) {
        writeFileToCache(versionNumber, file);
        return Integer.parseInt(versionNumber);
    }
}
