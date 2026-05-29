# Usamos una imagen de Java 17 como base
FROM openjdk:27-ea-slim-bookworm

ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools
# Esta línea es clave para que la caché se guarde donde tú quieres
ENV GRADLE_USER_HOME=/dist/.gradle

RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/tools.zip && \
    unzip /tmp/tools.zip -d $ANDROID_SDK_ROOT/cmdline-tools && \
    mv $ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools $ANDROID_SDK_ROOT/cmdline-tools/latest && \
    rm /tmp/tools.zip

RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app

# 1. Ejecuta la compilación
# 2. Crea la carpeta de destino en /dist (si no existe)
# 3. Copia el APK generado a la carpeta compartida
CMD ./gradlew assembleRelease --no-daemon && \
    mkdir -p /dist/android/release && \
    cp app/build/outputs/apk/release/*.apk /dist/android/release/