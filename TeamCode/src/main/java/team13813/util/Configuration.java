package team13813.util;

import java.util.ArrayList;

import team13813.io.GamepadManager;
import team13813.state.Facing;
import team13813.state.GoldPositions;
import team13813.state.SavingPath;
import team13813.state.State;
import team13813.state.Team;
import team13813.vision.MasterVision;

public class Configuration {
    /*
        String Initialization
     */
    public static final String FRONT_LEFT_MOTOR = "dm1";
    public static final String FRONT_RIGHT_MOTOR = "dm0";
    public static final String BACK_LEFT_MOTOR = "dm2";
    public static final String BACK_RIGHT_MOTOR = "dm3";
    public static final String ARM_LEFT_MOTOR = "um0";
    public static final String ARM_RIGHT_MOTOR = "um1";
    public static final String LIFT_MOTOR = "um2";

    public static final String FRONT_LEFT_SERVO = "ds0";
    public static final String FRONT_RIGHT_SERVO = "ds1";
    public static final String CLIP_SERVO = "ds2";

    public static final String LEFT_DISTANCE_SENSOR = "";
    public static final String RIGHT_DISTANCE_SENSOR = "";
    public static final String TOUCH_SENSOR = "";
    public static final String COLOR_SENSOR = "";

    /*
        STATE MACHINE
     */
    private static Team team;
    private static State state;
    private static Facing facing;
    private static GoldPositions goldPosition = GoldPositions.UNKNOWN;

    /*
        Settings
     */
    public static final MasterVision.TFLiteAlgorithm INFER = MasterVision.TFLiteAlgorithm.INFER_RIGHT;
    public static final Integer maximumStream = 5;

    public static final boolean ENCODER = false; //TODO update encoder
    public static final Integer TETRIX_TICKS_PER_REV = 1440;
    public static final Integer ANDYMARK_TICKS_PER_REV = 1120;
    public static final float ABSOLUTE_SPEED = 1.0f;

    /*
        Variables
     */
    public static ArrayList<GamepadManager> gamepadsTimeStream = new ArrayList<>();

    public static String getFileName() {
        assert getSavingPath() != null;
        return getSavingPath().toString() + "_LOG.txt";
    }
    private static SavingPath getSavingPath() {
        switch (team) {
            case RED:
                switch (facing) {
                    case WALLFACER:
                        switch (goldPosition) {
                            case CENTER:
                                return SavingPath.RedFacerCenter;
                            case LEFT:
                                return SavingPath.RedFacerLeft;
                            case RIGHT:
                                return SavingPath.RedFacerRight;
                        }
                    case WALLBREAKER:
                        switch (goldPosition) {
                            case CENTER:
                                return SavingPath.RedBreakerCenter;
                            case LEFT:
                                return SavingPath.RedBreakerLeft;
                            case RIGHT:
                                return SavingPath.RedBreakerRight;
                        }
                }
            case BLUE:
                switch (facing) {
                    case WALLFACER:
                        switch (goldPosition) {
                            case CENTER:
                                return SavingPath.BlueFacerCenter;
                            case LEFT:
                                return SavingPath.BlueFacerLeft;
                            case RIGHT:
                                return SavingPath.BlueFacerRight;
                        }
                    case WALLBREAKER:
                        switch (goldPosition) {
                            case CENTER:
                                return SavingPath.BlueBreakerCenter;
                            case LEFT:
                                return SavingPath.BlueBreakerLeft;
                            case RIGHT:
                                return SavingPath.BlueBreakerRight;
                        }
                }
        }
        return SavingPath.TEST;
    }

    public static Team getTeam() {
        return team;
    }
    public static void setTeam(Team team) {
        Configuration.team = team;
    }
    public static State getState() {
        return state;
    }
    public static void setState(State state) {
        Configuration.state = state;
    }
    public static Facing getFacing() {
        return facing;
    }
    public static void setFacing(Facing facing) {
        Configuration.facing = facing;
    }
    public static GoldPositions getGoldPosition() {
        return goldPosition;
    }
    public static void setGoldPosition(GoldPositions goldPosition) {
        Configuration.goldPosition = goldPosition;
    }
}
