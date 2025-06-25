package com.example.earthcanvas;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.util.HashMap;

public class AI  {

    // ログ用のタグ
    private final String TAG = this.getClass().getSimpleName();

    // TensorFlow Liteのインタープリター
    private final Interpreter interpreter;
    // モデルファイルのパス
    private static final String MODEL_PATH = "quickdraw_model2.tflite";
    // モデルの設定
    private static final int NUMBER_LENGTH = 5; // 出力ラベルの数
    private static final int DIM_BATCH_SIZE = 1; // バッチサイズ
    private static final int DIM_IMG_SIZE_X = 28; // 画像の幅
    private static final int DIM_IMG_SIZE_Y = 28; // 画像の高さ
    private static final int DIM_PIXEL_SIZE = 1; // ピクセルの次元数（グレースケール）
    private static final int BYTE_SIZE_OF_FLOAT = 4; // float型のバイト数

    private static final HashMap<Integer, String> answerMap = new HashMap<Integer, String>() {
        {
            put(0, "とり");
            put(1, "ねこ");
            put(2, "さかな");
            put(3, "たいよう");
            put(4, "き");
        }
    };

    /**
     * コンストラクタ
     * @param activity Activity
     */
    public AI(Activity activity) {
        // モデルファイルを読み込む
        this.interpreter = getInterpreter(activity);
    }

    /**
     * 推論を実行する
     * @param bitmap Bitmap
     * @return 推論結果
     */
    public String doClassify(Bitmap bitmap) {
        if (interpreter == null) {
            Log.e(TAG, "インタープリターが初期化されていません");
            return null;
        }

        // 入力データ用のバッファを作成
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * BYTE_SIZE_OF_FLOAT);
        inputBuffer.order(ByteOrder.nativeOrder());
        // 出力データ用のバッファを作成
        float[][] abcOutput = new float[DIM_BATCH_SIZE][NUMBER_LENGTH];

        // 入力データ用のバッファを作成
        inputBuffer = convertBitmapToByteBuffer(bitmap);

        // 推論を実行
        interpreter.run(inputBuffer, abcOutput);

        // 推論結果を取得
        float max = 0;
        int maxIndex = 0;
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            if (abcOutput[0][i] > max) {
                max = abcOutput[0][i];
                maxIndex = i;
            }
        }

        return answerMap.get(maxIndex);
    }

    /**
     * インタープリターを取得する
     * @param activity Activity
     * @return インタープリター
     */
    private Interpreter getInterpreter(Activity activity) {
        try (AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {

            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

            return new Interpreter(buffer);

        } catch (IOException e) {
            Log.e(TAG, "モデルファイルの読み込みに失敗しました", e);
        } catch (Exception e) {
            Log.e(TAG, "不明なエラーが発生しました", e);
        }
        return null;
    }


    /**
     * BitmapをByteBufferに変換する
     * @param bitmap Bitmap
     * @return ByteBuffer
     */
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // 入力データ用のバッファを作成
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * BYTE_SIZE_OF_FLOAT);
        byteBuffer.order(ByteOrder.nativeOrder());
        // バッファをクリア
        byteBuffer.rewind();
        // ピクセルデータをバッファにコピー
        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val & 0xFF) - 127.0f) / 127.0f);
            }
        }
        return byteBuffer;
    }
}