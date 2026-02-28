import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
import os

# TensorFlowバージョンの確認
print(tf.__version__)

from google.colab import drive
drive.mount('/content/drive')

# .npyファイルの読み込み関数
def load_quickdraw_data(categories):
    data = []
    labels = []
    for idx, category in enumerate(categories):#ラベルを付ける処理（0～4)
        print(category)
        file_path = f"/content/drive/MyDrive/quickdraw-dataset-master/quickdrawnpy/{category}"  # Adjust the path as necessary
        images = np.load(file_path)
        labels.extend([idx] * len(images))
        data.append(images)
    data = np.concatenate(data, axis=0)
    print(data)
    labels = np.array(labels)
    return data, labels

categories = ['bird.npy', 'cat.npy', 'fish.npy', 'sun.npy', 'tree.npy']
data, labels = load_quickdraw_data(categories)

# データのシャッフル
indices = np.arange(data.shape[0])
np.random.shuffle(indices)
data = data[indices]
labels = labels[indices]

# データの正規化
data = data / 255.0

# データの形状を調整 (28x28x1)
data = data.reshape(-1, 28, 28, 1)

# トレーニングデータとテストデータに分割
split_idx = int(data.shape[0] * 0.8)
train_data, test_data = data[:split_idx], data[split_idx:]
train_labels, test_labels = labels[:split_idx], labels[split_idx:]


model = keras.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),
    layers.Flatten(),
    layers.Dense(128, activation='relu'),
    layers.Dense(len(categories), activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(train_data, train_labels, epochs=5, validation_split=0.2)

# TensorFlow Lite Converterを使用してモデルを変換
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# TFLiteモデルの保存
with open('quickdraw_model.tflite', 'wb') as f:
    f.write(tflite_model)
