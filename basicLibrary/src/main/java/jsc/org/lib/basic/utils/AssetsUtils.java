/*
 * Copyright (c) 2022.
 *
 * Author: JiangShiCheng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jsc.org.lib.basic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class AssetsUtils {

    public static Bitmap loadAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (is != null) {
                is.close();
                is = null;
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyAssets(Context context, String fileName, File localFolder) {
        File file = new File(localFolder, fileName);
        if (file.exists()) return;
        try {
            boolean cr = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(context.getAssets().open(fileName));
            int len;
            byte[] buf = new byte[4096];
            while ((len = bis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fos.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
            boolean dr = file.delete();
        }
    }
}
