package team13813.vision

import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import team13813.state.GoldPositions

/**
 * Created by David Lukens on 10/31/2018.
 */
class TFLite(private val master: MasterVision) {
    companion object {
        private const val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
        private const val LABEL_GOLD_MINERAL = "Gold Mineral"
        private const val LABEL_SILVER_MINERAL = "Silver Mineral"
    }

    private var tfod: TFObjectDetector? = null
    private val tfodMoniterViewId = master.hMap.appContext.resources.getIdentifier("tfodMonitorViewId", "id", master.hMap.appContext.packageName)
    private val parameters = TFObjectDetector.Parameters(tfodMoniterViewId)

    fun init() {
        if (tfod == null) {
            tfod = ClassFactory.getInstance().createTFObjectDetector(parameters, master.vuforiaLocalizer)
            tfod?.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
        }
    }

    var lastKnownSampleOrder = GoldPositions.UNKNOWN

    internal fun updateSampleOrder() {
        if (tfod != null) {
            val updatedRecognitions = tfod?.updatedRecognitions

            val sorted = ArrayList<Recognition>()
            var first_score = 0f
            var second_score = 0f
            if (updatedRecognitions != null) {
                if (updatedRecognitions.size > 2) {
                    val sortedList = updatedRecognitions.sortedWith(compareByDescending({ it.confidence }))
//                    sortedList = sortedList.drop(2)

                    for (item in sortedList) {
                        if (item.confidence > first_score) {
                            first_score = item.confidence
                        } else if (item.confidence > second_score) {
                            second_score = item.confidence
                        }
                    }
                }
                for (item in updatedRecognitions) {
                    if (item.confidence >= second_score) {
                        sorted.add(item)
                    }
                }

                if (sorted.size == 3 || sorted.size == 2) {
                    var goldMineralX: Int? = null
                    var silverMineral1X: Int? = null
                    var silverMineral2X: Int? = null

                    for (recognition in sorted) {
                        if (recognition.label == LABEL_GOLD_MINERAL)
                            goldMineralX = recognition.left.toInt()
                        else if (silverMineral1X == null)
                            silverMineral1X = recognition.left.toInt()
                        else
                            silverMineral2X = recognition.left.toInt()
                    }
                    when (master.tfLiteAlgorithm) {
                        MasterVision.TFLiteAlgorithm.INFER_NONE -> if (goldMineralX != null && silverMineral1X != null && silverMineral2X != null)
                            if (sorted.size == 3)
                                lastKnownSampleOrder =
                                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X)
                                            GoldPositions.LEFT
                                        else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X)
                                            GoldPositions.RIGHT
                                        else
                                            GoldPositions.CENTER
                        MasterVision.TFLiteAlgorithm.INFER_LEFT -> {
                            if(sorted.size == 2) {
                                if (goldMineralX == null)
                                    lastKnownSampleOrder = GoldPositions.LEFT
                                else if (silverMineral1X != null)
                                    lastKnownSampleOrder =
                                            if (goldMineralX < silverMineral1X)
                                                GoldPositions.CENTER
                                            else
                                                GoldPositions.RIGHT
                            }
                        }
                        MasterVision.TFLiteAlgorithm.INFER_RIGHT -> {
                            if(sorted.size == 2) {
                                if (goldMineralX == null)
                                    lastKnownSampleOrder = GoldPositions.RIGHT
                                else if (silverMineral1X != null)
                                    lastKnownSampleOrder =
                                            if (goldMineralX < silverMineral1X)
                                                GoldPositions.LEFT
                                            else
                                                GoldPositions.CENTER
                            }
                        }
                    }
                }
            }
        }
    }

    fun enable() {
        tfod?.activate()
    }

    fun disable() {
        tfod?.deactivate()
    }

    fun shutdown() {
        tfod?.shutdown()
    }

}