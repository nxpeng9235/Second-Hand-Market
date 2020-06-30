package cn.scut.wg.secondhandmarket.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;

public class ImageUtil {

    public static void resize(String inputImgPath, String outputImgPath, int maxWidth, int maxHeight) throws IOException {
        Thumbnails.of(inputImgPath).size(maxWidth, maxHeight).toFile(outputImgPath);
    }

    public static void cut(String inputImgPath, String outputImgPath, int width, int height) throws Exception {
        Thumbnails.of(inputImgPath).sourceRegion(Positions.CENTER, width, height).keepAspectRatio(false).toFile(outputImgPath);
    }

}
