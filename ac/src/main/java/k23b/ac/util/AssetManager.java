package k23b.ac.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import android.content.Context;

public class AssetManager {

    public static String loadAsset(Context context, String filename) {

        BufferedReader br = null;

        try {

            InputStream is = context.getAssets().open(filename);

            InputStreamReader isr = new InputStreamReader(is);

            br = new BufferedReader(isr);

            String line;

            StringWriter sw = new StringWriter();

            String separator = System.getProperty("line.separator");

            while ((line = br.readLine()) != null) {
                sw.append(line);
                sw.append(separator);
            }

            return sw.toString();

        } catch (IOException e) {
            // e.printStackTrace();
            Logger.error(AssetManager.class.getSimpleName(), e.getMessage());

            return null;
        }
    }
}
