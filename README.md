# Put MACE Model in Storage Path
- Create **mace_workspace/models** direcotory in your SD card.
- Put **tf_resnet152.pb** and **tf_resnet152.data** in **mace_workspace/models**.

# Learn to Run MACE Model
Related code is in **app/src/main/java/com/example/macetestsample/MaceMultiModelTestTask.java**. Below code is a simple way to run a MACE model.
```java
// Create model
String deviceName = "CPU";
AppModel resnet152CPUModel = AppModel.maceCreateModel("tf_resnet152.pb", "tf_resnet152.data", deviceName);

// Prepare input data
float[] inputData = new float[1 * 224 * 224 * 3];

// Run model
float[] outputData = resnet152CPUModel.maceClassify(inputData);
```